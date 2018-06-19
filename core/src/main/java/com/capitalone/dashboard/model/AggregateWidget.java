package com.capitalone.dashboard.model;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

/**
 * Represents a widget on the Aggregate dashboard. Each widget is associated
 * with a specific component. The id, name and options should be provided by the
 * UI.
 */
public class AggregateWidget {
	private ObjectId dashboardId;
	private String name;
	private Map<String, Object> aggregateWidgets = new HashMap<>();

	public ObjectId getDashboardId() {
		return dashboardId;
	}

	public void setDashboardId(ObjectId dashboardId) {
		this.dashboardId = dashboardId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getAggregateWidgets() {
		return aggregateWidgets;
	}

	public void setAggregateWidgets(Map<String, Object> aggregateWidgets) {
		this.aggregateWidgets = aggregateWidgets;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		AggregateWidget widget = (AggregateWidget) o;

		return dashboardId.equals(widget.dashboardId);
	}

	@Override
	public int hashCode() {
		return dashboardId.hashCode();
	}
}
