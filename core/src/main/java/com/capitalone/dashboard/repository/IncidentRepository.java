package com.capitalone.dashboard.repository;


import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.IncidentDetails;
/**
 * Repository for {@link IncidentDetails}.
 */
public interface IncidentRepository extends CrudRepository<IncidentDetails, ObjectId>, QueryDslPredicateExecutor<IncidentDetails> {
	
	@Query(value = "{'assignmentGroup' : {$exists: true, $eq: ?0} }", delete = true)
	List<IncidentDetails> deleteIncident(String assignmentGroup);

}
