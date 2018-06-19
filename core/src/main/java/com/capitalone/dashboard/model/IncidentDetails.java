package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * CollectorItem extension to store incident details
 */
@Document(collection="incidents")
public class IncidentDetails extends BaseModel {
	
	private ObjectId collectorItemId;

	private String incidentNo;
	
	private String desc;
	
	private String ci;
	
	private String assignmentGroup;
	
	private long createdDate;
	
	private String requestedBy;
	
	private String state;
	
	private long closedDate;
	
	public ObjectId getCollectorItemId() {
		return collectorItemId;
	}

	public void setCollectorItemId(ObjectId collectorItemId) {
		this.collectorItemId = collectorItemId;
	}

	public String getIncidentNo() {
		return incidentNo;
	}

	public void setIncidentNo(String incidentNo) {
		this.incidentNo = incidentNo;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public String getAssignmentGroup() {
		return assignmentGroup;
	}

	public void setAssignmentGroup(String assignmentGroup) {
		this.assignmentGroup = assignmentGroup;
	}

	
	public long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}

	public String getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(String string) {
		this.requestedBy = string;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(long closedDate) {
		this.closedDate = closedDate;
	}
	
}
