package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="integration_tests")
public class IntegrationTestsResults extends BaseModel {
    private ObjectId collectorItemId;
    private long timestamp;

    private String name;
    private String url;
    private CodeQualityType type;
    private  String buildId;
    private  String overallFailed;
    private  String overallPassed;
    private  String overallTotal;
    private  String criticalFailed;
    private  String criticalPassed;
    private  String criticalTotal;
    private  String passPercentage;
    private  String skippedCount;
    private  String source;

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public CodeQualityType getType() {
        return type;
    }

    public void setType(CodeQualityType type) {
        this.type = type;
    }
    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

	public String getOverallFailed() {
		return overallFailed;
	}
	

	public void setOverallFailed(String overallFailed) {
		this.overallFailed = overallFailed;
	}
	

	public String getOverallPassed() {
		return overallPassed;
	}
	

	public void setOverallPassed(String overallPassed) {
		this.overallPassed = overallPassed;
	}
	

	public String getOverallTotal() {
		return overallTotal;
	}
	

	public void setOverallTotal(String overallTotal) {
		this.overallTotal = overallTotal;
	}
	

	public String getCriticalFailed() {
		return criticalFailed;
	}
	

	public void setCriticalFailed(String criticalFailed) {
		this.criticalFailed = criticalFailed;
	}
	

	public String getCriticalPassed() {
		return criticalPassed;
	}
	

	public void setCriticalPassed(String criticalPassed) {
		this.criticalPassed = criticalPassed;
	}
	

	public String getCriticalTotal() {
		return criticalTotal;
	}
	

	public void setCriticalTotal(String criticalTotal) {
		this.criticalTotal = criticalTotal;
	}
	

	public String getPassPercentage() {
		return passPercentage;
	}
	

	public void setPassPercentage(String passPercentage) {
		this.passPercentage = passPercentage;
	}

	public String getSkippedCount() {
		return skippedCount;
	}

	public void setSkippedCount(String skippedCount) {
		this.skippedCount = skippedCount;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
