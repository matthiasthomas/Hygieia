package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.Role;
import com.capitalone.dashboard.model.Scope;

/**
 * Repository for {@link Scope}.
 */
public interface RoleRepository extends CrudRepository<Role, ObjectId>,
		QueryDslPredicateExecutor<Role> {

	@Query(value = "{ name : ?0 }")
	Role findByName(String name);
}
