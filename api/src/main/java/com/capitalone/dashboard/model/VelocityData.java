package com.capitalone.dashboard.model;

public class VelocityData {
	private String month;
	private long estimated;
	private long actual;

	public VelocityData(long estimated, long actual) {
		super();
		this.estimated = estimated;
		this.actual = actual;
	}
	
	public VelocityData(String month, long estimated, long actual) {
		super();
		this.month = month;
		this.estimated = estimated;
		this.actual = actual;
	}

	public long getEstimated() {
		return estimated;
	}

	public void setEstimated(long estimated) {
		this.estimated = estimated;
	}

	public long getActual() {
		return actual;
	}

	public void setActual(long actual) {
		this.actual = actual;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}
}
