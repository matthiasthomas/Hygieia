package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.IntegrationTestsResults;

public interface IntegrationTestRepository extends CrudRepository<IntegrationTestsResults, ObjectId>, QueryDslPredicateExecutor<IntegrationTestsResults> {
	@Query(value="{'collectorItemId': ?0 }")
	List<IntegrationTestsResults> getIntegrationTestsResultsByCollectorItemId(ObjectId collectorId);
}
