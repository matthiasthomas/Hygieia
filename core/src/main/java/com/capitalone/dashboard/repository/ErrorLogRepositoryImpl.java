package com.capitalone.dashboard.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;

import com.capitalone.dashboard.model.DateWiseCount;
import com.capitalone.dashboard.model.ErrorLog;
import com.capitalone.dashboard.model.ModuleWiseCount;

public class ErrorLogRepositoryImpl implements ErrorLogRepositoryCustom {

	@Autowired
	private MongoOperations operations;

	@Override
	public int deleteAllCreateDateLessThan(long createDate) {

		BasicQuery query = new BasicQuery("{}");
		query.addCriteria(Criteria.where("timestamp").lte(createDate));

		return operations.remove(query, ErrorLog.class).getN();
	}

	@Override
	public List<ModuleWiseCount> groupByModule() {
		Aggregation agg = newAggregation(
				group("module").count().as("total"),
				project("total").and("module").previousOperation(),
				sort(Sort.Direction.DESC, "total"));
		AggregationResults<ModuleWiseCount> groupResults = operations
				.aggregate(agg, ErrorLog.class, ModuleWiseCount.class);
		return groupResults.getMappedResults();
	}

	@Override
	public List<DateWiseCount> groupByDate() {
		Aggregation agg = newAggregation(
				group("createDate").count().as("total"),
				project("total").and("createDate").previousOperation(),
				sort(Sort.Direction.DESC, "total"));
		AggregationResults<DateWiseCount> groupResults = operations
				.aggregate(agg, ErrorLog.class, DateWiseCount.class);
		return groupResults.getMappedResults();
	}
}
