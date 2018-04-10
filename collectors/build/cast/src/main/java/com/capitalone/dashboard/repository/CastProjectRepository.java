package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CastProject;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CastProjectRepository extends BaseCollectorItemRepository<CastProject> {

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.appId : ?2}")
    CastProject findCastProject(ObjectId collectorId, String instanceUrl, String projectId);

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<CastProject> findEnabledProjects(ObjectId collectorId, String instanceUrl);
}
