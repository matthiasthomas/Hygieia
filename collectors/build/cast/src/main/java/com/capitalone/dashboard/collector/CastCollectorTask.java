/**
 * 
 */
package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CastCollector;
import com.capitalone.dashboard.model.CastProject;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.ConfigHistOperationType;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CastCollectorRepository;
import com.capitalone.dashboard.repository.CastProjectRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.ComponentRepository;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CastCollectorTask extends CollectorTask<CastCollector> {
	@SuppressWarnings({ "PMD.UnusedPrivateField", "unused" })
	private static final Log LOG = LogFactory.getLog(CastCollectorTask.class);
	private final CastCollectorRepository castCollectorRepository;
	private final CastProjectRepository castProjectRepository;
	private final CodeQualityRepository codeQualityRepository;
	private final CastClientSelector castClientSelector;
	private final CastSettings castSettings;
	private final ComponentRepository dbComponentRepository;

	@Autowired
	public CastCollectorTask(TaskScheduler taskScheduler, CastCollectorRepository castCollectorRepository,
			CastProjectRepository castProjectRepository, CodeQualityRepository codeQualityRepository,
			CastSettings castSettings, CastClientSelector castClientSelector,
			ComponentRepository dbComponentRepository) {
		super(taskScheduler, "Cast");
		this.castCollectorRepository = castCollectorRepository;
		this.castProjectRepository = castProjectRepository;
		this.codeQualityRepository = codeQualityRepository;
		this.castSettings = castSettings;
		this.dbComponentRepository = dbComponentRepository;
		this.castClientSelector = castClientSelector;
	}

	@Override
	public CastCollector getCollector() {
		return CastCollector.prototype(castSettings.getServers(), castSettings.getVersions(),
				castSettings.getMetrics());
	}

	@Override
	public BaseCollectorRepository<CastCollector> getCollectorRepository() {
		return castCollectorRepository;
	}

	@Override
	public String getCron() {
		return castSettings.getCron();
	}
	/*		@Value("${cron}") // Injected from application.properties
			private String cron;
			
			@Value("${apiToken}") // Injected from application.properties
			private String apiToken;*/

	public void collect(CastCollector collector) {
		long start = System.currentTimeMillis();

		Set<ObjectId> udId = new HashSet<>();
		udId.add(collector.getId());
		List<CastProject> existingProjects = castProjectRepository.findByCollectorIdIn(udId);
		List<CastProject> latestApplications = new ArrayList<>();
		clean(collector, existingProjects);

		if (!CollectionUtils.isEmpty(collector.getCastServers())) {

			for (int i = 0; i < collector.getCastServers().size(); i++) {

				String instanceUrl = collector.getCastServers().get(i);
				Double version = collector.getCastVersions().get(i);

				logBanner(instanceUrl);
				CastClient castClient = castClientSelector.getCastClient(version);
				List<CastProject> projects = castClient.getApplications(instanceUrl);
				for (CastProject p : projects) {
					LOG.info(p.getApplicationId() + " : " + p.getApplicationName());
				}
				latestApplications.addAll(projects);

				int projSize = ((CollectionUtils.isEmpty(projects)) ? 0 : projects.size());
				log("Fetched projects   " + projSize, start);

				addNewProjects(projects, existingProjects, collector);

				// List<CastProject> eenabledProjects = enabledProjects(collector, instanceUrl);

				refreshData(projects, castClient);

				log("Finished", start);
			}
		}
	}

	/**
	 * Clean up unused sonar collector items
	 *
	 * @param collector
	 *            the {@link SonarCollector}
	 */

	private List<CastProject> enabledProjects(CastCollector collector, String instanceUrl) {
		return castProjectRepository.findEnabledProjects(collector.getId(), instanceUrl);
	}

	private void refreshData(List<CastProject> castProjects, CastClient castClient) {
		long start = System.currentTimeMillis();
		int count = 0;

		for (CastProject project : castProjects) {
			CodeQuality codeQuality = castClient.currentCodeQuality(project);
			if (codeQuality != null && isNewQualityData(project, codeQuality)) {
				codeQuality.setCollectorItemId(project.getId());
				codeQualityRepository.save(codeQuality);
				count++;
			}
		}
		log("Updated", start, count);
	}

	@SuppressWarnings("PMD")
	private void clean(CastCollector collector, List<CastProject> existingProjects) {
		Set<ObjectId> uniqueIDs = new HashSet<>();
		for (com.capitalone.dashboard.model.Component comp : dbComponentRepository.findAll()) {
			if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
				List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.CodeQuality);
				if (itemList != null) {
					for (CollectorItem ci : itemList) {
						if (ci != null && ci.getCollectorId().equals(collector.getId())) {
							uniqueIDs.add(ci.getId());
						}
					}
				}
			}
		}
		List<CastProject> stateChangeJobList = new ArrayList<>();
		Set<ObjectId> udId = new HashSet<>();
		udId.add(collector.getId());
		for (CastProject job : existingProjects) {
			// collect the jobs that need to change state : enabled vs disabled.
			if ((job.isEnabled() && !uniqueIDs.contains(job.getId())) || // if it was enabled but not on a dashboard
					(!job.isEnabled() && uniqueIDs.contains(job.getId()))) { // OR it was disabled and now on a dashboard
				job.setEnabled(uniqueIDs.contains(job.getId()));
				stateChangeJobList.add(job);
			}
		}
		if (!CollectionUtils.isEmpty(stateChangeJobList)) {
			castProjectRepository.save(stateChangeJobList);
		}
	}

	// @SuppressWarnings("unused")
	// private List<CastProject> enabledProjects(CastCollector collector) {
	// 	return castProjectRepository.findEnabledProjects(collector.getId());

	// }

	@SuppressWarnings("unused")
	private void addNewProjects(List<CastProject> projects, List<CastProject> existingProjects,
			CastCollector collector) {
		long start = System.currentTimeMillis();
		int count = 0;
		List<CastProject> newProjects = new ArrayList<>();
		for (CastProject project : projects) {
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
			castProjectRepository.save(newProjects);
		}
		log("New projects", start, count);
	}

	// @SuppressWarnings("unused")
	// private boolean isNewProject(CastCollector collector, CastProject application) {
	// 	return castProjectRepository.findCastProject(collector.getId(), application.getApplicationId()) == null;
	// }

	@SuppressWarnings("unused")
	private boolean isNewQualityData(CastProject project, CodeQuality codeQuality) {
		return codeQualityRepository.findByCollectorItemIdAndTimestamp(project.getId(),
				codeQuality.getTimestamp()) == null;
	}

	@SuppressWarnings("unused")
	private long convertToTimestamp(String date) {

		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
		DateTime dt = formatter.parseDateTime(date);
		long d = new DateTime(dt).getMillis();

		return d;
	}

	@SuppressWarnings("unused")
	private ConfigHistOperationType determineConfigChangeOperationType(String changeAction) {
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