package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="gitkpi_collector_items")
public class GitKpiCollectorItem extends BaseModel  {
    private ObjectId repoCollectorItemId;
	private ObjectId collectorId;
    private long lastUpdated;
    private Map<String,GitStats> daywiseCommits=new HashMap<>();
    private Map<String,Object> options = new HashMap<>();
    public static final String TEAMSIZE = "teamSize";
    public static final String CONTRIBUTION_PCT = "contributionPct";
    public static final String DAYWISE_COMMITS = "daywiseCommits";
    public static final String TEAM = "team";
    public static final String REPO_URL = "url"; 
    public static final String BRANCH = "branch"; 

    public ObjectId getRepoCollectorItemId() {
		return repoCollectorItemId;
	}

	public void setRepoCollectorItemId(ObjectId repoCollectorItemId) {
		this.repoCollectorItemId = repoCollectorItemId;
	}

	public Integer getTeamSize() {
        return (Integer) options.get(TEAMSIZE);
    }

    public void setTeamSize(Integer teamSize) {
        options.put(TEAMSIZE, teamSize);
    }

    public Double getContributionPCT() {
        return (Double) options.get(CONTRIBUTION_PCT);
    }

    public void setContributionPCT(Double pct) {
        options.put(CONTRIBUTION_PCT, pct);
    }
    
    public Map<String,GitStats> getDaywiseCommits() {
        return daywiseCommits;
    }

    public void setDaywiseCommits(Map<String,GitStats> commits) {
    	daywiseCommits.clear();
    	if(commits!=null)
    		daywiseCommits.putAll(commits);
    }
    
    public String getRepoUrl() {
        return (String) options.get(REPO_URL);
    }

    public void setRepoUrl(String instanceUrl) {
        options.put(REPO_URL, instanceUrl);
    }
    
    public List<String> getTeam() {
        return (List<String>) options.get(TEAM);
    }

    public void setTeam(List<String> team) {
        options.put(TEAM, team);
    }
    public void setTeam(Set<String> team) {
        options.put(TEAM, new ArrayList<String>(team));
    }
    
    public String getBranch() {
        return (String) options.get(BRANCH);
    }

    public void setBranch(String branch) {
        options.put(BRANCH, branch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
        	return true;
        }
        if (o == null || getClass() != o.getClass()) {
        	return false;
        }

        GitKpiCollectorItem gitHubRepo = (GitKpiCollectorItem) o;

        return getRepoUrl().equals(gitHubRepo.getRepoUrl()) & getBranch().equals(gitHubRepo.getBranch()) & getRepoCollectorItemId().equals(gitHubRepo.getRepoCollectorItemId());
    }

    @Override
    public int hashCode() {
        return getRepoUrl().hashCode();
    }
}
