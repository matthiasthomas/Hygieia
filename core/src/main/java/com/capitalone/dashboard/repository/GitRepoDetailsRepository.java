package com.capitalone.dashboard.repository;

import java.util.List;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.capitalone.dashboard.model.GitRepoDetails;
import com.capitalone.dashboard.model.GlobalConfiguration;

public interface GitRepoDetailsRepository extends
		PagingAndSortingRepository<GitRepoDetails, String>,
		QueryDslPredicateExecutor<GlobalConfiguration> {

	/**
	 * Search for top 10 repositories that contains search text either in repoName or repoURL field.
	 * 
	 * @param repoName search text for repository name
	 * @param repoUrl search text for repository URL
	 * @return List of top 10 repository that matches search text.
	 */
	List<GitRepoDetails> findTop10ByRepoNameLikeOrRepoUrlLikeAllIgnoringCaseOrderByRepoNameAsc(
			String repoName, String repoUrl);

	/**
	 * Deletes all repository entries that has older updated date.
	 * 
	 * @param updatedDt date when repository was created/updated 
	 */
	void deleteInBulkByUpdatedDtLessThan(long updatedDt);
}
