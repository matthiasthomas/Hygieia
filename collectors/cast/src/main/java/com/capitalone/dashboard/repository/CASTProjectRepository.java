package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.CASTProject;

public interface CASTProjectRepository extends
		BaseCollectorItemRepository<CASTProject> {

	@Query(value = "{ 'collectorId' : ?0, options.projectUrl : ?1, options.projectName : ?2}")
	CASTProject findCASTProject(ObjectId collectorId, String projectUrl,
			String projectName);

	@Query(value = "{ 'collectorId' : ?0, enabled: true}")
	List<CASTProject> findEnabledProjects(ObjectId collectorId);
}
