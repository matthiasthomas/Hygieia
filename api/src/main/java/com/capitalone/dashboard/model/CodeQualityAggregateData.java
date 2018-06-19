package com.capitalone.dashboard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

public class CodeQualityAggregateData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5272295167910782140L;
	private ObjectId id;
	private List<String> infoData;
	private String name;
	private List<Map<String,Object>> metrics;
	
	public CodeQualityAggregateData(ObjectId id)
	{
		super();
		metrics = new ArrayList<Map<String,Object>>();
		infoData = new ArrayList<String>();
		this.id = id;
	}
	@SuppressWarnings({"CPD-START"})
	public CodeQualityAggregateData(ObjectId id, String name,
			List<Map<String,Object>> metrics) {
		super();
		this.id = id;
		this.name = name;
		this.metrics = metrics;
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Map<String,Object>> getMetrics() {
		return metrics;
	}
	public void setMetrics(List<Map<String,Object>> metrics) {
		this.metrics = metrics;
	}

	@SuppressWarnings({"PMD.NPathComplexity", "CPD-END"})
	public void addMetrics(CodeQuality codeQuality)
	{
		Map<String, Object> map=new HashMap<String, Object>();
		Set<CodeQualityMetric> data = codeQuality.getMetrics();
		if(data != null){	
			for (CodeQualityMetric metric : data){
				if(metric.getName().equals("duplicated_lines_density")) {
					map.put("duplications", metric.getName().equals("duplicated_lines_density")? metric.getValue():"0");
				} else if (metric.getName().equals("ncloc")) {
					map.put("size",  metric.getName().equals("ncloc")? metric.getValue():"0");
				} else if (metric.getName().equals("coverage")) {
					map.put("coverage",  metric.getName().equals("coverage")? metric.getValue():"0");
				} else if (metric.getName().equals("alert_status")) {
					map.put("qualityGate",  metric.getName().equals("alert_status")? metric.getValue():"");
				}
				/*
				 * This was for history before we switched to above values
				 * 
				map.put("blocker", history.get("blocker_violations"));
				map.put("critical", history.get("critical_violations"));
				map.put("major", history.get("major_violations"));
				map.put("issues", history.get("violations"));*/
			}
			if(!map.containsKey("duplications")){
				map.put("duplications","0");
			}
			if(!map.containsKey("size")){
				map.put("size","0");
			}
			if(!map.containsKey("coverage")){
				map.put("coverage","0");
			}
			if(!map.containsKey("qualityGate")){
				map.put("qualityGate","");
			}
			map.put("name", codeQuality.getName());
			metrics.add(map);
		}
	}
	public List<String> getInfoData() {
		return infoData;
	}
	public void setInfoData(List<String> infoData) {
		this.infoData = infoData;
	}
	public void addInfoData(CodeQuality codeQuality){
		String data = codeQuality.getName();
		if(data != null){
			infoData.add(data);
		}
	}
}
