package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.UnitTestsResults;

/**
 * Repository for {@link CodeQuality} data.
 */
public interface UnitTestsRepository extends CrudRepository<UnitTestsResults, ObjectId>, QueryDslPredicateExecutor<UnitTestsResults> {
	
	UnitTestsResults findByCollectorItemIdAndTimestamp(ObjectId collectorItemId, long timestamp);
	
	@Query(value="{'collectorItemId': ?0 }")
	List<UnitTestsResults> getUnitTestsResultsByCollectorItemId(ObjectId collectorId);
}
