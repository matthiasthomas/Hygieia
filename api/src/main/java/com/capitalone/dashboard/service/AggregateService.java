package com.capitalone.dashboard.service;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.AggregateWidget;

public interface AggregateService {

	AggregateWidget getAggregateWidgetByDashboardId(ObjectId dashboardId,int offset);

}
