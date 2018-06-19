package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.capitalone.dashboard.model.ErrorLog;

//This repo is be used for DB logging functionality. 
public interface ErrorLogRepository extends PagingAndSortingRepository<ErrorLog, ObjectId>,ErrorLogRepositoryCustom,QueryDslPredicateExecutor<ErrorLog>{

	@Query
	List<ErrorLog> findByTimestampGreaterThan(
			long timestamp);
	
	@Query
	List<ErrorLog> findByTimestampLessThan(
			long createDate);	
}
