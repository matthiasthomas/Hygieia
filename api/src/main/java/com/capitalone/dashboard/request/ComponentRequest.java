package com.capitalone.dashboard.request;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;

/**
 * Created by yaf107 on 1/11/16.
 */
public class ComponentRequest {
    @NotNull
    private ObjectId componentId;
    
    @NotNull
    private CollectorType collectorType;
    
    private List<CollectorItem> collectorItems;

	public ObjectId getComponentId() {
		return componentId;
	}

	public void setComponentId(ObjectId componentId) {
		this.componentId = componentId;
	}

	public CollectorType getCollectorType() {
		return collectorType;
	}

	public void setCollectorType(CollectorType collectorType) {
		this.collectorType = collectorType;
	}

	public List<CollectorItem> getCollectorItems() {
		return collectorItems;
	}

	public void setCollectorItems(List<CollectorItem> collectorItems) {
		this.collectorItems = collectorItems;
	}
}
