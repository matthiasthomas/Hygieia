package com.capitalone.dashboard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

public class ServiceNowAggregateData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	private ObjectId id;
	private List<String> infoData;
	private String name;
	private Collection<SNAggregatedData> metrics;
	
	public ServiceNowAggregateData(ObjectId id)
	{
		super();
		metrics = new ArrayList<SNAggregatedData>();
		infoData = new ArrayList<String>();
		this.id = id;
	}
	@SuppressWarnings({"CPD-START"})
	public ServiceNowAggregateData(ObjectId id, String name,
			Collection<SNAggregatedData> metrics) {
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
	public Collection<SNAggregatedData> getMetrics() {
		if (metrics == null) {
			return new ArrayList<SNAggregatedData>();
		}
		return metrics;
	}
	public void setMetrics(List<SNAggregatedData> metrics) {
		this.metrics = metrics;
	}

	@SuppressWarnings({ "CPD-END" })
	public void addMetrics(SNAssignmentGroup assignmentGroup) {
		
		Map<String, SNAggregatedData> map = new HashMap<String, SNAggregatedData>();

		if (assignmentGroup.getIncidentHistory() != null) {
			
			// Prepare map of current metrics available.
			for (SNAggregatedData metricsOfMonth : getMetrics()) {
				map.put(metricsOfMonth.getMonth(), metricsOfMonth);
			}
			
			// Add metrics data of assignment group. 
			Object monthData = null;
			List<SNAggregatedData> assgGrpDataList = assignmentGroup
					.getIncidentHistory();
			SNAggregatedData snAggregatedData = null;
			for (SNAggregatedData assgGrpData : assgGrpDataList) {
				monthData = map.get(assgGrpData.getMonth());
				if (monthData == null) {
					snAggregatedData = new SNAggregatedData();
					snAggregatedData.setMonth(assgGrpData.getMonth());
					snAggregatedData.setCountOpened(assgGrpData
							.getCountOpened());
					snAggregatedData.setCountClosed(assgGrpData
							.getCountClosed());
					map.put(assgGrpData.getMonth(), snAggregatedData);
				} else {
					snAggregatedData = (SNAggregatedData) monthData;
					snAggregatedData.setCountOpened(snAggregatedData
							.getCountOpened() + assgGrpData.getCountOpened());
					snAggregatedData.setCountClosed(snAggregatedData
							.getCountClosed() + assgGrpData.getCountClosed());
				}
			}
			this.metrics = map.values();
		}
	}
	
	public List<String> getInfoData() {
		return infoData;
	}
	public void setInfoData(List<String> infoData) {
		this.infoData = infoData;
	}
	public void addInfoData(SNAssignmentGroup assignmentGroup){
		String data = assignmentGroup.getAssignmentGroup();
		if(data != null){
			infoData.add(data);
		}
	}

}
