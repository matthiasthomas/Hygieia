package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents release version and notes
 */
@Document(collection = "release")
public class Release extends BaseModel {
	
	private long projectId;
	
	private String releaseId;
	
	private String version;
	
	private long releaseDate;
		
	private String notes;
	
	public Release() {
	}
	
	public Release(long projectId, String releaseId, String version, long releaseDate, String notes) {
		this.projectId = projectId;
		this.releaseId = releaseId;
		this.version = version;
		this.releaseDate = releaseDate;
		this.notes = notes;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(String releaseId) {
		this.releaseId = releaseId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(long releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public String toString() {
		return "Release [projectId=" + projectId + ", releaseId=" + releaseId
				+ ", version=" + version + ", releaseDate=" + releaseDate
				+ ", notes=" + notes + "]";
	}

}
