package com.capitalone.dashboard.model.jira;

public class Sprint {
	private long sprintId;
	private String name;
	private String state;
	private String goal;
	private long startDate;
	private long endDate;
	private long completeDate;
	private int completedIssues;
	private int totalIssues;
	private int estimated;
	private int completed;
	private double rejectionRate;
	private int bugsCreated;

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public Sprint(long sprintId, String name, String state, String goal,
			long startDate, long endDate, long completeDate,
			int completedIssues, int estimated, int completed) {
		super();
		this.sprintId = sprintId;
		this.name = name;
		this.state = state;
		this.goal = goal;
		this.startDate = startDate;
		this.endDate = endDate;
		this.completeDate = completeDate;
		this.completedIssues = completedIssues;
		this.estimated = estimated;
		this.completed = completed;
	}

	public Sprint() {
		
	}

	public long getSprintId() {
		return sprintId;
	}

	public void setSprintId(long sprintId) {
		this.sprintId = sprintId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public long getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(long completeDate) {
		this.completeDate = completeDate;
	}

	public int getCompletedIssues() {
		return completedIssues;
	}

	public void setCompletedIssues(int completedIssues) {
		this.completedIssues = completedIssues;
	}

	public int getEstimated() {
		return estimated;
	}

	public void setEstimated(int estimated) {
		this.estimated = estimated;
	}

	public int getCompleted() {
		return completed;
	}

	public void setCompleted(int completed) {
		this.completed = completed;
	}

	public int getTotalIssues() {
		return totalIssues;
	}

	public void setTotalIssues(int totalIssues) {
		this.totalIssues = totalIssues;
	}

	public double getRejectionRate() {
		return rejectionRate;
	}

	public void setRejectionRate(double rejectionRate) {
		this.rejectionRate = rejectionRate;
	}

	public int getBugsCreated() {
		return bugsCreated;
	}

	public void setBugsCreated(int bugsCreated) {
		this.bugsCreated = bugsCreated;
	}	
}
