package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.CodeQualityMeasures;

/**
 * Repository for {@link CodeQualityMeasures} data.
 */
public interface CodeQualityHistoryRepository
		extends CrudRepository<CodeQualityMeasures, ObjectId>, QueryDslPredicateExecutor<CodeQualityMeasures> {

	/**
	 * Finds the {@link CodeQualityMeasures} data point at the given timestamp for a
	 * specific {@link com.capitalone.dashboard.model.CollectorItem}.
	 *
	 * @param collectorItemId
	 *            collector item id
	 * @param timestamp
	 *            timstamp
	 * @return a {@link CodeQualityMeasures}
	 */
	CodeQualityMeasures findByCollectorItemIdAndTimestamp(ObjectId collectorItemId, long timestamp);

	CodeQualityMeasures findByCollectorItemId(ObjectId collectorItemId);
}
