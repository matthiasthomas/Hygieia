package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.CASTCollector;
import com.capitalone.dashboard.model.CASTProject;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.GlobalConfiguration;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CASTProjectRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GlobalConfigurationRepository;
import com.capitalone.dashboard.util.ApplicationDBLogger;
import com.capitalone.dashboard.util.HygieiaConstants;

/**
 * CollectorTask that fetches code quality parameters for CAST
 */
@Component
public class CASTCollectorTask extends CollectorTask<CASTCollector> {
	private static final Log LOG = LogFactory.getLog(CASTCollectorTask.class);

	private final BaseCollectorRepository<CASTCollector> castRepository;
	private final CASTClient castClient;
	private final CASTSettings castSettings;
	private final CodeQualityRepository codeQualityRepository;
	private final ComponentRepository dbComponentRepository;
	private final CASTProjectRepository castProjectRepository;
	private final GlobalConfigurationRepository globalConfigurationRepository;

	@Autowired
	public CASTCollectorTask(TaskScheduler taskScheduler,
			CASTSettings castSettings,
			BaseCollectorRepository<CASTCollector> castRepository,
			CASTProjectRepository castProjectRepository,
			CodeQualityRepository codeQualityRepository, CASTClient castClient,
			ComponentRepository dbComponentRepository,GlobalConfigurationRepository globalConfigurationRepository) {
		super(taskScheduler, "CAST");
		this.castSettings = castSettings;
		this.castRepository = castRepository;
		this.castClient = castClient;
		this.codeQualityRepository = codeQualityRepository;
		this.dbComponentRepository = dbComponentRepository;
		this.castProjectRepository = castProjectRepository;
		this.globalConfigurationRepository = globalConfigurationRepository;
	}

	/**
	 * Accessor method for the collector prototype object
	 */
	@Override
	public CASTCollector getCollector() {
		return CASTCollector.prototype();
	}

	@Override
	public BaseCollectorRepository<CASTCollector> getCollectorRepository() {
		return castRepository;
	}

	@Override
	public String getCron() {
		return castSettings.getCron();
	}

	private void clean(Collector collector) {
		Set<ObjectId> uniqueIDs = new HashSet<>();
		/**
		 * Logic: For each component, retrieve the collector item list of the
		 * type SCM. Store their IDs in a unique set ONLY if their collector IDs
		 * match with GitHub collectors ID.
		 */
		for (com.capitalone.dashboard.model.Component comp : dbComponentRepository.findAll()) {
			if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
				List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.CAST);
				List<CollectorItem> productItemList = comp.getCollectorItems().get(CollectorType.ProductCAST);
				
				if (CollectionUtils.isEmpty(itemList))continue;
				for (CollectorItem ci : itemList) {
					if (ci != null && ci.getCollectorId().equals(collector.getId())) {
						uniqueIDs.add(ci.getId());
					}
				}
				
				if (productItemList != null){
	                for (CollectorItem ci : productItemList) {
	                    if (collector.getId().equals(ci.getCollectorId())) {
	                        uniqueIDs.add(ci.getId());
	                    }
	                }
                }
			}
		}

		/**
		 * Logic: Get all the collector items from the collector_item collection
		 * for this collector. If their id is in the unique set (above), keep
		 * them enabled; else, disable them.
		 */
		List<CASTProject> repoList = new ArrayList<>();
		Set<ObjectId> gitID = new HashSet<>();
		Set<ObjectId> gitRepoItemID = new HashSet<>();
		gitID.add(collector.getId());
		for (CASTProject repo : castProjectRepository
				.findByCollectorIdIn(gitID)) {
			boolean flag = uniqueIDs.contains(repo.getId());
			if (repo.isEnabled() != flag)
				LOG.info("Status Changed to " + flag + " for"
						+ repo.getDescription());
			repo.setEnabled(flag);
			repoList.add(repo);
			gitRepoItemID.add(repo.getId());
		}
		castProjectRepository.save(repoList);
	}
	private void refresh(){
		/* Kept for feature use for refreshing CAST Data Run Time */
		GlobalConfiguration config = globalConfigurationRepository
				.findByKey("refreshCAST");
		if (config != null) {
			String refreshCAST = config.getValue();
			if ("yes".equals(refreshCAST)) {
				LOG.info("CAST:REFRESH");
				config.setValue("no");
				globalConfigurationRepository.save(config);
				codeQualityRepository.deleteByType("CAST");
				
			}
		}
		
	}
	

	@Override
	public void collect(CASTCollector collector) {
		try {
			logBanner("Starting CAST ...");
			refresh();
			clean(collector);
			long start = System.currentTimeMillis();

			// Fetch Existing
			Set<ObjectId> udId = new HashSet<>();
			udId.add(collector.getId());
			Set<CASTProject> existingProjects = new HashSet(
					castProjectRepository.findByCollectorIdIn(udId));

			// Sync CAST Projects
			Set<CASTProject> latestProjects = new HashSet(
					castClient.getCASTProjects(collector.getId()));

			// Get New projects and Save
			latestProjects.removeAll(existingProjects);
			if (latestProjects.size() > 0)
				castProjectRepository.save(latestProjects);
			log("New projects", start, latestProjects.size());

			//TODO:Currently fetching data for all projects later we will fetch all enabled projects only
			List<CASTProject> existingProjectList = castProjectRepository
					.findByCollectorIdIn(udId);

			for (CASTProject castProject : existingProjectList) {
				CodeQuality codeQuality = castClient
						.getQualityDetails(castProject);
				CodeQuality codeQualityCurrent = codeQualityRepository
						.findByCollectorItemId(castProject.getId());
				if (codeQualityCurrent != null)
					codeQuality.setId(codeQualityCurrent.getId());

				codeQualityRepository.save(codeQuality);
			}

			log("Finished", start);
		} catch (Exception e) {
			ApplicationDBLogger.log(HygieiaConstants.CAST_COLLECTOR,
					"CASTCollectorTask.collect",
					"Error getting data from CAST", e);
			LOG.error("Error getting data from CAST" + e);
		}
	}
}