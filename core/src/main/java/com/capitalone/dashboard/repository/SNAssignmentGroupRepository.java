package com.capitalone.dashboard.repository;


import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.IncidentDetails;
import com.capitalone.dashboard.model.SNAssignmentGroup;
/**
 * Repository for {@link SNAssignmentGroup}.
 */
public interface SNAssignmentGroupRepository extends BaseCollectorItemRepository<SNAssignmentGroup>{
	
	@Query(value = "{'_class' : 'com.capitalone.dashboard.model.SNAssignmentGroup'}", delete = true)
	  List<IncidentDetails> deleteAssignmentGroups();

	@Query(value="{'_class' : ?0}")
	   List<CollectorItem> findAssignmentGroupsByClass(String collectorClass);
	
	   @Query(value="{ 'collectorId' : ?0, enabled: true}")
	    List<SNAssignmentGroup> findEnabledAssignmentGroups(ObjectId collectorId);
}
