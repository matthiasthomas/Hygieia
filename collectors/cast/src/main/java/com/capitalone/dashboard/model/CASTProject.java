package com.capitalone.dashboard.model;

public class CASTProject extends CollectorItem {
	protected static final String PROJETC_URL = "projectUrl";
	protected static final String PROJECT_NAME = "projectName";

	public String getProjectUrl() {
		return (String) getOptions().get(PROJETC_URL);
	}

	public void setProjectUrl(String instanceUrl) {
		getOptions().put(PROJETC_URL, instanceUrl);
	}

	public String getProjectName() {
		return (String) getOptions().get(PROJECT_NAME);
	}

	public void setProjectName(String name) {
		getOptions().put(PROJECT_NAME, name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		CASTProject that = (CASTProject) o;
		return getProjectName().equals(that.getProjectName())
				&& getProjectUrl().equals(that.getProjectUrl());
	}

	@Override
	public int hashCode() {
		int result = getProjectUrl().hashCode();
		return result;
	}
}
