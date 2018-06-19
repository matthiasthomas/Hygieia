package com.capitalone.dashboard.collector;

import java.util.List;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.CASTProject;
import com.capitalone.dashboard.model.CodeQuality;

/**
 * Client for fetching data from CAST
 */
public interface CASTClient {
	List<CASTProject> getCASTProjects(ObjectId collectorId);

	CodeQuality getQualityDetails(CASTProject castProject);
}
