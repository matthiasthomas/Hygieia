package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.GitRepoDetails;

public interface GitRepoDetailsService {

	/**
	 * Provides list of all repositories and it's associated branches that
	 * matches search text. The search text is matched with repo name and repo
	 * URL and all repositories that contains either repo name or repo URL will
	 * be provided as a result.
	 * 
	 * @param text
	 *            search text for repository.
	 * @return DataResponse containing list of all repositories where either
	 *         repo name or repo URL contains search text.
	 */
	DataResponse<Iterable<GitRepoDetails>> search(String text);
}