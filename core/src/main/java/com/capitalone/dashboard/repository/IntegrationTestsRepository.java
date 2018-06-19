package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.IntegrationTestsResults;

/**
 * Repository for {@link CodeQuality} data.
 */
public interface IntegrationTestsRepository extends CrudRepository<IntegrationTestsResults, ObjectId>, QueryDslPredicateExecutor<IntegrationTestsResults> {
	
	IntegrationTestsResults findByCollectorItemIdAndTimestamp(ObjectId collectorItemId, long timestamp);
}
