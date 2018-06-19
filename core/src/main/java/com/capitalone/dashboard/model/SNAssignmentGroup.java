package com.capitalone.dashboard.model;

import java.util.List;

/**
 * CollectorItem extension to store the servicenow Assignment Groups.
 */
public class SNAssignmentGroup extends CollectorItem {
	
	protected static final String ASSIGNMENT_GROUP = "assignmentGroup";
	protected static final String INCIDENT_HISTORY = "incidentHistory";

	protected static final String TIME = "time";
	
	
	public String getAssignmentGroup() {
		return (String) getOptions().get(ASSIGNMENT_GROUP);
	}

	public void setAssignmentGroup(String assignmentGroup) {
		getOptions().put(ASSIGNMENT_GROUP, assignmentGroup);
	}
	public List<SNAggregatedData> getIncidentHistory() {
		return (List<SNAggregatedData>) getOptions().get(INCIDENT_HISTORY);
	}

	public void setIncidentHistory(List<SNAggregatedData> incHistory) {
		getOptions().put(INCIDENT_HISTORY, incHistory);
	}
	
		
}
