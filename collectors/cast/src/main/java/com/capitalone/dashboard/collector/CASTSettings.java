package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean to hold settings specific to the CAST collector.
 */
@Component
@ConfigurationProperties(prefix = "cast")
public class CASTSettings {
	private String cron;
	private String url;
	private String credentials;
	private String projectListSuffix;
	private String qualityIndicators;
	
	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public String getProjectListSuffix() {
		return projectListSuffix;
	}

	public void setProjectListSuffix(String projectListSuffix) {
		this.projectListSuffix = projectListSuffix;
	}

	public String getQualityIndicators() {
		return qualityIndicators;
	}

	public void setQualityIndicators(String qualityIndicators) {
		this.qualityIndicators = qualityIndicators;
	}
}
