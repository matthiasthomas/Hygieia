	/*### Spring Singleton
	--------------------------------------
	
	The CollectorTask class is a Spring bean singleton. Use the constructor to inject any Spring beans that are required to
	execute the logic of your collector (eg MongDB repositories such as FeatureRepository).
	package com.capitalone.dashboard.collector;*/
	
	package com.capitalone.dashboard.collector;
	
	import com.capitalone.dashboard.model.CastAIPCollector;
	import com.capitalone.dashboard.model.CastAIPProject;
	import com.capitalone.dashboard.model.CodeQuality;
	import com.capitalone.dashboard.model.CollectorItem;
	import com.capitalone.dashboard.model.CollectorItemConfigHistory;
	import com.capitalone.dashboard.model.CollectorType;
	import com.capitalone.dashboard.model.ConfigHistOperationType;
	import com.capitalone.dashboard.repository.BaseCollectorRepository;
	import com.capitalone.dashboard.repository.CastAIPCollectorRepository;
	import com.capitalone.dashboard.repository.CastAIPProfileRepository;
	import com.capitalone.dashboard.repository.CastAIPProjectRepository;
	import com.capitalone.dashboard.repository.CodeQualityRepository;
	import com.capitalone.dashboard.repository.ComponentRepository;
	
	import org.apache.commons.collections.CollectionUtils;
	import org.apache.commons.logging.Log;
	import org.apache.commons.logging.LogFactory;
	import org.bson.types.ObjectId;
	import org.json.simple.JSONArray;
	import org.json.simple.JSONObject;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.scheduling.TaskScheduler;
	import org.springframework.stereotype.Component;
	import org.joda.time.DateTime;
	import org.joda.time.format.DateTimeFormat;
	import org.joda.time.format.DateTimeFormatter;
	
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.HashSet;
	import java.util.List;
	import java.util.Map;
	import java.util.Set;
	
	@Component
	public class CastAIPCollectorTask extends CollectorTask<CastAIPCollector>{
		private static final Log LOG = LogFactory.getLog(CastAIPCollectorTask.class);
	    private final CastAIPCollectorRepository castAIPCollectorRepository;
	    private final CastAIPProjectRepository castAIPProjectRepository;
	    private final CodeQualityRepository codeQualityRepository;
	    private final CastAIPProfileRepository castAIPProfileRepository;
	    private final CastAIPClientSelector castAIPClientSelector;
	    private final CastAIPSettings castAIPSettings;
	    private final ComponentRepository dbComponentRepository;
	
	    @Autowired
	    public CastAIPCollectorTask(TaskScheduler taskScheduler,
	    							CastAIPCollectorRepository castAIPCollectorRepository,
	    							CastAIPProjectRepository castAIPProjectRepository,
	    							CodeQualityRepository codeQualityRepository,
	    							CastAIPProfileRepository castAIPProfileRepository,
	    							CastAIPSettings castAIPSettings,
	    							CastAIPClientSelector castAIPClientSelector,
	    							ComponentRepository dbComponentRepository) {
	        super(taskScheduler, "Cast");
	        this.castAIPCollectorRepository = castAIPCollectorRepository;
	        this.castAIPProjectRepository = castAIPProjectRepository;
	        this.codeQualityRepository = codeQualityRepository;
	        this.castAIPProfileRepository = castAIPProfileRepository;
	        this.castAIPSettings = castAIPSettings;
	        this.castAIPClientSelector = castAIPClientSelector;
	        this.dbComponentRepository = dbComponentRepository;
	    }
	    @Override
	    public CastAIPCollector getCollector() {
	        return CastAIPCollector.prototype(castAIPSettings.getServers(), castAIPSettings.getVersions(), castAIPSettings.getMetrics());
	    }
	
	    @Override
	    public BaseCollectorRepository<CastAIPCollector> getCollectorRepository() {
	        return castAIPCollectorRepository;
	    }
	
	    @Override
	    public String getCron() {
	        return castAIPSettings.getCron();
	    }
		@Value("${cron}") // Injected from application.properties
		private String cron;
		
		@Value("${apiToken}") // Injected from application.properties
		private String apiToken;
		
		public void collect(CastAIPCollector collector) {
	        long start = System.currentTimeMillis();
	
	        Set<ObjectId> udId = new HashSet<>();
	        udId.add(collector.getId());
	        List<CastAIPProject> existingProjects = castAIPProjectRepository.findByCollectorIdIn(udId);
	        List<CastAIPProject> latestProjects = new ArrayList<>();
	        clean(collector, existingProjects);
	
	        if (!CollectionUtils.isEmpty(collector.getCastAIPServers())) {
	            
	            for (int i = 0; i < collector.getCastAIPServers().size(); i++) {
	
	                String instanceUrl = collector.getCastAIPServers().get(i);
	                Double version = collector.getCastAIPVersions().get(i);
	                String metrics = collector.getCastAIPMetrics().get(i);
	
	                logBanner(instanceUrl);
	               // CastAIPProject castClient = castAIPClientSelector.getCastClient(version);
	                CastAIPClient castClient = castAIPClientSelector.getCastClient(version);
	                List<CastAIPProject> projects = castClient.getProjects(instanceUrl);
	                latestProjects.addAll(projects);
	
	                int projSize = ((CollectionUtils.isEmpty(projects)) ? 0 : projects.size());
	                log("Fetched projects   " + projSize, start);
	
	                addNewProjects(projects, existingProjects, collector);
	
	                refreshData(enabledProjects(collector, instanceUrl), castClient,metrics);
	                
	                // Changelog apis do not exist for sonarqube versions under version 5.0
	                if (version >= 5.0) {
	                  try {
	                     fetchQualityProfileConfigChanges(collector,instanceUrl,castClient);
	                   } catch (Exception e) {
	                	   
	                	  LOG.error(e);
	                    }
	                }
	
	                log("Finished", start);
	            }
	        }
	        deleteUnwantedJobs(latestProjects, existingProjects, collector);
	    }
	
	
	/**
	 * Clean up unused sonar collector items
	 *
	 * @param collector
	 *            the {@link SonarCollector}
	 */
	
	private void clean(CastAIPCollector collector, List<CastAIPProject> existingProjects) {
	    Set<ObjectId> uniqueIDs = new HashSet<>();
	    for (com.capitalone.dashboard.model.Component comp : dbComponentRepository
	            .findAll()) {
	        if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
	            List<CollectorItem> itemList = comp.getCollectorItems().get(
	                    CollectorType.CodeQuality);
	            if (itemList != null) {
	                for (CollectorItem ci : itemList) {
	                    if (ci != null && ci.getCollectorId().equals(collector.getId())) {
	                        uniqueIDs.add(ci.getId());
	                    }
	                }
	            }
	        }
	    }
	    List<CastAIPProject> stateChangeJobList = new ArrayList<>();
	    Set<ObjectId> udId = new HashSet<>();
	    udId.add(collector.getId());
	    for (CastAIPProject job : existingProjects) {
	        // collect the jobs that need to change state : enabled vs disabled.
	        if ((job.isEnabled() && !uniqueIDs.contains(job.getId())) ||  // if it was enabled but not on a dashboard
	                (!job.isEnabled() && uniqueIDs.contains(job.getId()))) { // OR it was disabled and now on a dashboard
	            job.setEnabled(uniqueIDs.contains(job.getId()));
	            stateChangeJobList.add(job);
	        }
	    }
	    if (!CollectionUtils.isEmpty(stateChangeJobList)) {
	    	castAIPProjectRepository.save(stateChangeJobList);
	    }
	}
	
	private void deleteUnwantedJobs(List<CastAIPProject> latestProjects, List<CastAIPProject> existingProjects, CastAIPCollector collector) {
	    List<CastAIPProject> deleteJobList = new ArrayList<>();
	
	    // First delete collector items that are not supposed to be collected anymore because the servers have moved(?)
	    for (CastAIPProject job : existingProjects) {
	        if (job.isPushed()) continue; // do not delete jobs that are being pushed via API
	        if (!collector.getCastAIPServers().contains(job.getInstanceUrl()) ||
	                (!job.getCollectorId().equals(collector.getId())) ||
	                (!latestProjects.contains(job))) {
	            deleteJobList.add(job);
	        }
	    }
	    if (!CollectionUtils.isEmpty(deleteJobList)) {
	    	castAIPProjectRepository.delete(deleteJobList);
	    }
	}
	
	
	@SuppressWarnings("unused")
	private void refreshData(List<CastAIPProject> castProjects, CastAIPClient castClient, String metrics) {
	    long start = System.currentTimeMillis();
	    int count = 0;
	
	    for (CastAIPProject project : castProjects) {
	        CodeQuality codeQuality = castClient.currentCodeQuality(project, metrics);
	        if (codeQuality != null && isNewQualityData(project, codeQuality)) {
	            codeQuality.setCollectorItemId(project.getId());
	            codeQualityRepository.save(codeQuality);
	            count++;
	        }
	    }
	    log("Updated", start, count);
	}
	
	private void fetchQualityProfileConfigChanges(CastAIPCollector collector,String instanceUrl,CastAIPClient castClient) throws org.json.simple.parser.ParseException{
		JSONArray qualityProfiles = castClient.getQualityProfiles(instanceUrl);   
		JSONArray castAIPProfileConfigurationChanges = new JSONArray();
	    
		for (Object qualityProfile : qualityProfiles ) {      	
			JSONObject qualityProfileJson = (JSONObject) qualityProfile;
			String qualityProfileKey = (String)qualityProfileJson.get("key");
	
			List<String>castAIPProjects = castClient.retrieveProfileAndProjectAssociation(instanceUrl,qualityProfileKey);
			if (castAIPProjects != null){
				castAIPProfileConfigurationChanges = castClient.getQualityProfileConfigurationChanges(instanceUrl,qualityProfileKey);
				addNewConfigurationChanges(collector,castAIPProfileConfigurationChanges);
			}
		}
	}
	
	private void addNewConfigurationChanges(CastAIPCollector collector,JSONArray castAIPProfileConfigurationChanges){
		ArrayList<CollectorItemConfigHistory> profileConfigChanges = new ArrayList();
		
		for (Object configChange : castAIPProfileConfigurationChanges) {		
			JSONObject configChangeJson = (JSONObject) configChange;
			CollectorItemConfigHistory profileConfigChange = new CollectorItemConfigHistory();
			Map<String,Object> changeMap = new HashMap<String,Object>();
			
			profileConfigChange.setCollectorItemId(collector.getId());
			profileConfigChange.setUserName((String) configChangeJson.get("authorName"));
			profileConfigChange.setUserID((String) configChangeJson.get("authorLogin") );
			changeMap.put("event", configChangeJson);
	
			profileConfigChange.setChangeMap(changeMap);
			
			ConfigHistOperationType operation = determineConfigChangeOperationType((String)configChangeJson.get("action"));
			profileConfigChange.setOperation(operation);
			
				
			long timestamp = convertToTimestamp((String) configChangeJson.get("date"));
			profileConfigChange.setTimestamp(timestamp);
			
			if (isNewConfig(collector.getId(),(String) configChangeJson.get("authorLogin"),operation,timestamp)) {
				profileConfigChanges.add(profileConfigChange);
			}
		}
		castAIPProfileRepository.save(profileConfigChanges);
	}
	
	private Boolean isNewConfig(ObjectId collectorId,String authorLogin,ConfigHistOperationType operation,long timestamp) {
		List<CollectorItemConfigHistory> storedConfigs = castAIPProfileRepository.findProfileConfigChanges(collectorId, authorLogin,operation,timestamp);
		return storedConfigs.isEmpty();
	}

	private List<CastAIPProject> enabledProjects(CastAIPCollector collector, String instanceUrl) {
	    return castAIPProjectRepository.findEnabledProjects(collector.getId(), instanceUrl);

	}
	
	
	private void addNewProjects(List<CastAIPProject> projects, List<CastAIPProject> existingProjects, CastAIPCollector collector) {
	    long start = System.currentTimeMillis();
	    int count = 0;
	    List<CastAIPProject> newProjects = new ArrayList<>();
	    for (CastAIPProject project : projects) {
	        if (!existingProjects.contains(project)) {
	            project.setCollectorId(collector.getId());
	            project.setEnabled(false);
	            project.setDescription(project.getApplicationName());
	            newProjects.add(project);
	            count++;
	        }
	    }
	    //save all in one shot
	    if (!CollectionUtils.isEmpty(newProjects)) {
	        castAIPProjectRepository.save(newProjects);
	    }
	    log("New projects", start, count);
	}
	
	@SuppressWarnings("unused")
	private boolean isNewProject(CastAIPCollector collector, CastAIPProject application) {
	    return castAIPProjectRepository.findcastAIPProject(
	            collector.getId(), application.getInstanceUrl(), application.getApplicationId()) == null;
	}
	
	private boolean isNewQualityData(CastAIPProject project, CodeQuality codeQuality) {
	    return codeQualityRepository.findByCollectorItemIdAndTimestamp(
	            project.getId(), codeQuality.getTimestamp()) == null;
	}
	
	private long convertToTimestamp(String date) {
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
		DateTime dt = formatter.parseDateTime(date);
		long d = new DateTime(dt).getMillis();
		
		return d;	
	}
	
	private ConfigHistOperationType determineConfigChangeOperationType(String changeAction){
		switch (changeAction) {
		
	    	case "DEACTIVATED":
	    		return ConfigHistOperationType.DELETED;
	    		
	    	case "ACTIVATED":
	    		return ConfigHistOperationType.CREATED;
	    	default:
	    		return ConfigHistOperationType.CHANGED;
		}	
	}	
	
}