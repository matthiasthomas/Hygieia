package com.capitalone.dashboard.model;

public class JobCollectorItem extends CollectorItem {
	protected static final String INSTANCE_URL = "instanceUrl";
	protected static final String JOB_NAME = "jobName";
	protected static final String JOB_URL = "jobUrl";
	protected static final String DEPLOYMENT_FREQUENCY = "deploymentFrequency";
	protected static final String DEPLOYMENT_SUCCESS_RATE = "deploymentSuccessRate";
	protected static final String BUILD_DURATION = "deploymentSpeed";
	protected static final String SUCCESS_BUILD_COUNT = "numberOfSuccessCount";

	public String getInstanceUrl() {
		return (String) getOptions().get(INSTANCE_URL);
	}

	public void setInstanceUrl(String instanceUrl) {
		getOptions().put(INSTANCE_URL, instanceUrl);
	}

	public String getJobName() {
		return (String) getOptions().get(JOB_NAME);
	}

	public void setJobName(String jobName) {
		getOptions().put(JOB_NAME, jobName);
	}

	public String getJobUrl() {
		return (String) getOptions().get(JOB_URL);
	}

	public void setJobUrl(String jobUrl) {
		getOptions().put(JOB_URL, jobUrl);
	}

	public String getDeploymentFrequency() {
		return (String) getOptions().get(DEPLOYMENT_FREQUENCY);
	}

	public void setDeploymentFrequency(String deploymentFrequency) {
		getOptions().put(DEPLOYMENT_FREQUENCY, deploymentFrequency);
	}

	public String getDeploymentSuccessRate() {
		return (String) getOptions().get(DEPLOYMENT_SUCCESS_RATE);
	}

	public void setDeploymentSuccessRate(String deploymentSuccessRate) {
		getOptions().put(DEPLOYMENT_SUCCESS_RATE, deploymentSuccessRate);
	}

	public String getBuildDuration() {
		return (String) getOptions().get(BUILD_DURATION);
	}

	public void setBuildDuration(String buildDuration) {
		getOptions().put(BUILD_DURATION, buildDuration);
	}
	
	public String getSuccessBuildCount() {
		return (String) getOptions().get(SUCCESS_BUILD_COUNT);
	}

	public void setSuccessBuildCount(String buildDuration) {
		getOptions().put(SUCCESS_BUILD_COUNT, buildDuration);
	}
}
