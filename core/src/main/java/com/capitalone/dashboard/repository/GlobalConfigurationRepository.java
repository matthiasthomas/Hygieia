package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.GlobalConfiguration;
import com.capitalone.dashboard.model.Scope;

/**
 * Repository for {@link Scope}.
 */
public interface GlobalConfigurationRepository extends CrudRepository<GlobalConfiguration, ObjectId>,
		QueryDslPredicateExecutor<GlobalConfiguration> {

	@Query(value = "{ key : ?0 }")
	GlobalConfiguration findByKey(String key);
}
