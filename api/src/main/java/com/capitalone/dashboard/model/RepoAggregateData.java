package com.capitalone.dashboard.model;

import java.io.Serializable;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

public class RepoAggregateData implements Serializable{
	private static final long serialVersionUID = 4975681714356553568L;
	private ObjectId id;
	private Map<String, GitStats> changes = new HashMap<String, GitStats>();  	
	private Set<String> infoData=new HashSet();
	private DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMM");
	 
	public RepoAggregateData(ObjectId id)
	{
		super();
		this.id = id;
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public Collection<GitStats> getMetrics() {
		return changes.values();
	}
	
	public Set<String> getInfoData() {
		return infoData;
	}
	public void setInfoData(Set<String> infoData) {
		this.infoData = infoData;
	}
	public void addRepo(GitKpiCollectorItem collectorItem)
	{
		if(collectorItem!=null && collectorItem.getDaywiseCommits() != null)
		{
			Map<String, GitStats> map = collectorItem.getDaywiseCommits();
			Set<String> dates = new HashSet(map.keySet());
			dates.addAll(changes.keySet());
			for(String date:dates)
			{
				if(changes.containsKey(date) && map.containsKey(date)) {
					changes.get(date).addGitStats(map.get(date));
				} else if (!changes.containsKey(date)) {
						changes.put(date, map.get(date).updateDate(date));
				}
			}
			infoData.add(collectorItem.getRepoUrl());
		}
	}
	
	//Add current month if not present
	public void setCurrentMonthIfNotPresent()
	{		
		String date = format.print(System.currentTimeMillis());
		GitStats gitStats = GitStats.initiate(date);
		if(!changes.containsKey(date))
			changes.put(date, gitStats);
	}
}
