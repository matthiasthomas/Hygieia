package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents code quality at a specific point in time. This could include
 * a unit test run, a security scan, static analysis, functional tests,
 * manual acceptance tests or bug reports.
 *
 * Possible Collectors:
 *  Sonar (in scope)
 *  Fortify
 *  ALM
 *  Various build system test results
 *
 */
@Document(collection="code_quality_history")
public class CodeQualityMeasures extends BaseModel {
    private ObjectId collectorItemId;
    private String name; 
    private long timestamp;
    private Map<String, List<CodeQualityHistory>> history = new HashMap<String, List<CodeQualityHistory>>();
    
    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

	public Map<String, List<CodeQualityHistory>> getHistory() {
		return history;
	}

	public void setHistory(Map<String, List<CodeQualityHistory>> history) {
		this.history = history;
	}

}

