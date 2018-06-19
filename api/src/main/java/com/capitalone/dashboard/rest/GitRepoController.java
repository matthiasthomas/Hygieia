package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.GitRepoDetails;
import com.capitalone.dashboard.request.GitRequestRequest;
import com.capitalone.dashboard.service.GitKPIService;
import com.capitalone.dashboard.service.GitRepoDetailsService;

@RestController
public class GitRepoController {

	private final GitKPIService gitKPIService;

	private final GitRepoDetailsService gitRepoDetailsService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GitRepoController.class); //NOPMD

	@Autowired
	public GitRepoController(GitKPIService gitKPIService,
			GitRepoDetailsService gitRepoDetailsService) {
		this.gitKPIService = gitKPIService;
		this.gitRepoDetailsService = gitRepoDetailsService;
	}

	@RequestMapping(value = "/kpi", method = GET, produces = APPLICATION_JSON_VALUE)
	public DataResponse<Map<String, Object>> search(
			@Valid GitRequestRequest request) {
		return gitKPIService.fetchKpi(request);
	}

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
	@RequestMapping(value = "/repos", method = GET, produces = APPLICATION_JSON_VALUE)
	public DataResponse<Iterable<GitRepoDetails>> search(
			@ModelAttribute("searchText") String text) {
		return gitRepoDetailsService.search(text);
	}

}