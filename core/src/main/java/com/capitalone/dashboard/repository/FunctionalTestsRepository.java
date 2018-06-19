package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.FunctionalTestsResults;
import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link CodeQuality} data.
 */
public interface FunctionalTestsRepository extends CrudRepository<FunctionalTestsResults, ObjectId>, QueryDslPredicateExecutor<FunctionalTestsResults> {
	
	FunctionalTestsResults findByCollectorItemIdAndTimestamp(ObjectId collectorItemId, long timestamp);
}
