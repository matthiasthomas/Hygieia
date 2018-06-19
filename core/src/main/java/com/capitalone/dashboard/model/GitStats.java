package com.capitalone.dashboard.model;

public class GitStats {
	private int commits;
	private int pulls;
	private int issues;
	private String date;
	public int getCommits() {
		return commits;
	}
	public void setCommits(int commits) {
		this.commits = commits;
	}
	public int getPulls() {
		return pulls;
	}
	public void setPulls(int pulls) {
		this.pulls = pulls;
	}
	public int getIssues() {
		return issues;
	}
	public void setIssues(int issues) {
		this.issues = issues;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	private GitStats configureDate(String date) {
		this.date = date;
		return this;
	}
	public GitStats updateDate(String date) {
		this.date = date;
		return this;
	}
	
	public GitStats addCommits()
	{
		commits++;
		return this;
	}
	public GitStats addIssues()
	{
		issues++;
		return this;
	}
	public GitStats addPulls()
	{
		pulls++;
		return this;
	}
	
	public GitStats addGitStats(GitStats stats)
	{
		commits += stats.getCommits();
		pulls += stats.getPulls();
		issues += stats.getIssues();
		return this;
	}
	
	public static GitStats initiate(String date)
	{
		return new GitStats().configureDate(date);
	}
	
	public static GitStats initiateCommit()
	{
		return new GitStats().addCommits();
	}
	public static GitStats initiateIssues()
	{
		return new GitStats().addIssues();
	}
	public static GitStats initiatePulls()
	{
		return new GitStats().addPulls();
	}	
}
