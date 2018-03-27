package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CastAIPProject;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CastAIPProjectRepository extends BaseCollectorItemRepository<CastAIPProject> {

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.projectId : ?2}")
    CastAIPProject findcastAIPProject(ObjectId collectorId, String instanceUrl, String projectId);

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<CastAIPProject> findEnabledProjects(ObjectId collectorId, String instanceUrl);
}
