package com.capitalone.dashboard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ProjectVelocityAggregateData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -641680341374250387L;
	private ObjectId id;
	private List<String> infoData;
	private String name;
	private List<VelocityData> metrics;
	private DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMM");

	public ProjectVelocityAggregateData(ObjectId id) {
		super();
		metrics = new ArrayList<VelocityData>();
		infoData = new ArrayList<String>();
		this.id = id;
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

	public List<VelocityData> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<VelocityData> metrics) {
		this.metrics = metrics;
	}

	public void addMetrics(Map<String, VelocityData> map) {
		for (Entry<String, VelocityData> entry : map.entrySet()) {
			metrics.add(entry.getValue());
		}
	}

	public List<String> getInfoData() {
		return infoData;
	}

	public void setInfoData(List<String> infoData) {
		this.infoData = infoData;
	}

	public void addInfoData(String projectName) {
		if (projectName != null) {
			infoData.add(projectName);
		}
	}

	// Add current month if not present
	public void setCurrentMonthIfNotPresent() {
		String date = format.print(System.currentTimeMillis());
		boolean exists = false;
		if (metrics != null)
			exists = metrics.stream().anyMatch(a -> date.equals(a.getMonth()));
		else
			metrics = new ArrayList<VelocityData>();

		//Add only if metrics is having some data
		if (!exists && metrics.size() > 0)
			metrics.add(new VelocityData(date, 0, 0));
	}
}
