package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.FunctionalTestsResults;

public interface FunctionalTestRepository extends CrudRepository<FunctionalTestsResults, ObjectId>, QueryDslPredicateExecutor<FunctionalTestsResults> {
	@Query(value="{'collectorItemId': ?0 }")
	List<FunctionalTestsResults> getFunctionalTestsResultsByCollectorItemId(ObjectId collectorId);
}
