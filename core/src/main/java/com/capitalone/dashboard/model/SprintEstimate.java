package com.capitalone.dashboard.model;

public class SprintEstimate {
	private double openEstimate;
	private double inProgressEstimate;
	private double completeEstimate;
	private double totalEstimate;
	public double getOpenEstimate() {
		return openEstimate;
	}
	public void setOpenEstimate(double openEstimate) {
		this.openEstimate = openEstimate;
	}
	public double getInProgressEstimate() {
		return inProgressEstimate;
	}
	public void setInProgressEstimate(double inProgressEstimate) {
		this.inProgressEstimate = inProgressEstimate;
	}
	public double getCompleteEstimate() {
		return completeEstimate;
	}
	public void setCompleteEstimate(double completeEstimate) {
		this.completeEstimate = completeEstimate;
	}
	public double getTotalEstimate() {
		return totalEstimate;
	}
	public void setTotalEstimate(double totalEstimate) {
		this.totalEstimate = totalEstimate;
	}
}
