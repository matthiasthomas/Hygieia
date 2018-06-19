package com.capitalone.dashboard.model;

public class ModuleWiseCount {

	private String module;
	private long total;
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	@Override
	public String toString() {
		return "[module=" + module + ", total=" + total + "]";
	}
}
