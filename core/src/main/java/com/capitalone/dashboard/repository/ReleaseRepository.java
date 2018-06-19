package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.Release;

public interface ReleaseRepository extends CrudRepository<Release, ObjectId>,
		QueryDslPredicateExecutor<Pipeline> {

	/**
	 * Provides all releases ordered by latest first
	 * 
	 * @return List of all releases ordered by latest first
	 */
	List<Release> findByOrderByReleaseDateDesc();

	/**
	 * Provides release notes for release of given project.
	 * 
	 * @param projectId
	 *            Id of project for which release notes are sought
	 * @param releaseId
	 *            Id of release for which release notes are sought
	 * @return Provides release notes for given project id and release id.
	 */
	List<Release> findByProjectIdAndReleaseId(long projectId, String releaseId);

}
