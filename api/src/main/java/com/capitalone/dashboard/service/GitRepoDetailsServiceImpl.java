package com.capitalone.dashboard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.GitRepoDetails;
import com.capitalone.dashboard.repository.GitRepoDetailsRepository;

@Service
public class GitRepoDetailsServiceImpl implements GitRepoDetailsService {

	private final GitRepoDetailsRepository gitRepoDetailsRepository;

	@Autowired
	public GitRepoDetailsServiceImpl(
			GitRepoDetailsRepository gitRepoDetailsRepository) {
		this.gitRepoDetailsRepository = gitRepoDetailsRepository;
	}

	@Override
	public DataResponse<Iterable<GitRepoDetails>> search(String text) {
		List<GitRepoDetails> gitRepoDetails = this.gitRepoDetailsRepository
				.findTop10ByRepoNameLikeOrRepoUrlLikeAllIgnoringCaseOrderByRepoNameAsc(text, text);
		return new DataResponse<Iterable<GitRepoDetails>>(gitRepoDetails, 0);
	}

}
