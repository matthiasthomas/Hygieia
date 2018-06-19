package com.capitalone.dashboard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

public class BuildAggregateData implements Serializable{

	private static final long serialVersionUID = 8224370708119019572L;
	private ObjectId id;
	private double avgSuccessRate;
	private List<Map> metrics;
	private Set<String> infoData=new HashSet();	
	private double totalDeploymentSuccessRate;
	
	public BuildAggregateData(ObjectId id)
	{
		super();
		metrics = new ArrayList<Map>();
		this.id = id;
	}
	public BuildAggregateData(ObjectId id, double avgSuccessRate,
			List<Map> metrics) {
		super();
		this.id = id;
		this.avgSuccessRate = avgSuccessRate;
		this.metrics = metrics;
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public double getAvgSuccessRate() {
		return avgSuccessRate;
	}
	public void setAvgSuccessRate(double avgSuccessRate) {
		this.avgSuccessRate = avgSuccessRate;
	}
	public List<Map> getMetrics() {
		return metrics;
	}
	public void setMetrics(List<Map> metrics) {
		this.metrics = metrics;
	}
	
	public Set<String> getInfoData() {
		return infoData;
	}
	public void setInfoData(Set<String> infoData) {
		this.infoData = infoData;
	}
	public void addJob(CollectorItem collectorItem)
	{
		Map map=new HashMap();
		Map options = collectorItem.getOptions();
		
		if(options!=null){
			double deploymentSuccessRate = 0.0;
			if (options.containsKey("deploymentSuccessRate")) {
				deploymentSuccessRate = NumberUtils.parseNumber((String)options.get("deploymentSuccessRate"), Double.class);
			}
			
			int numberOfBuilds = 0;
			if (options.containsKey("deploymentFrequency")) {
				numberOfBuilds = NumberUtils.parseNumber((String)options.get("deploymentFrequency"), Integer.class);
			}
			
			String url = (String)options.get("jobUrl");
			
			totalDeploymentSuccessRate +=deploymentSuccessRate;
			map.put("jobUrl", options.get("jobUrl"));
			
			int numberOfSuccessCount = 0;
			if (options.containsKey("numberOfSuccessCount")) {
				numberOfSuccessCount = NumberUtils.parseNumber((String)options.get("numberOfSuccessCount"), Integer.class);
			} else {
				numberOfSuccessCount = (int)((numberOfBuilds * 100.0) / deploymentSuccessRate);
			}
			
			map.put("numberOfSuccessCount", numberOfSuccessCount);
			map.put("numberOfBuilds", numberOfBuilds);
			map.put("deploymentSuccessRate", deploymentSuccessRate);
			metrics.add(map);
			if(!StringUtils.isEmpty(url))
				infoData.add(url);
			
			avgSuccessRate = (totalDeploymentSuccessRate /(metrics.size()*1.0));
		}
	}
}
