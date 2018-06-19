package com.capitalone.dashboard.model;

public class SNAggregatedData {
	
	private int countOpened;
    private int countClosed;
    private String month;

	public int getCountOpened() {
		return countOpened;
	}
	public void setCountOpened(int countOpened) {
		this.countOpened = countOpened;
	}
	public int getCountClosed() {
		return countClosed;
	}
	public void setCountClosed(int countClosed) {
		this.countClosed = countClosed;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	@Override
	public String toString() {
		return "SNAggregatedData [countOpened=" + countOpened
				+ ", countClosed=" + countClosed + ", month=" + month + "]";
	}
	
}
