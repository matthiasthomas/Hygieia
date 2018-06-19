package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;

public class ServiceNowSearchRequest {
    @NotNull
    private ObjectId componentId;
    private ObjectId collectorItemId;
    private Integer numberOfDays;
   
    public ObjectId getComponentId() {
        return componentId;
    }

    public void setComponentId(ObjectId componentId) {
        this.componentId = componentId;
    }
    
    public ObjectId getCollectorItemId() {
		return collectorItemId;
	}

	public void setCollectorItemId(ObjectId collectorItemId) {
		this.collectorItemId = collectorItemId;
	}

    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
    }
}