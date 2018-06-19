package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This model represents GitHub repository and it's associated branches.
 * @author JMehta
 *
 */
@Document(collection = "git_repo_details")
public class GitRepoDetails {
	
	private String id;

	private String org;
	
	private String repoName;

	private String repoUrl;
	
	private long updatedDt;

	private List<String> branches;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getRepoName() {
		return repoName;
	}

	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}

	public String getRepoUrl() {
		return repoUrl;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	public List<String> getBranches() {
		return branches;
	}
	
	public long getUpdatedDt() {
		return updatedDt;
	}

	public void setUpdatedDt(long updatedDt) {
		this.updatedDt = updatedDt;
	}

	public void setBranches(List<String> branches) {
		this.branches = branches;
	}
	
	public void addBranches(List<String> branches) {
		if (this.branches == null) {
			this.branches = new ArrayList<String>();
		}
		this.branches.addAll(branches);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		GitRepoDetails gitHubRepoDetails = (GitRepoDetails) o;

		return getId().equals(gitHubRepoDetails.getId())
				&& getOrg().equals(gitHubRepoDetails.getOrg())
				&& getRepoName().equals(gitHubRepoDetails.getRepoName())
				&& getRepoUrl().equals(gitHubRepoDetails.getRepoUrl())
				&& getBranches().equals(gitHubRepoDetails.getBranches());
	}

	@Override
	public int hashCode() {
		return getRepoUrl().hashCode();
	}

}
