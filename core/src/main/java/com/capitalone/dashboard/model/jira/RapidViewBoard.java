package com.capitalone.dashboard.model.jira;

import java.util.ArrayList;
import java.util.List;

public class RapidViewBoard {
	private int boardId;
	private String name;
	private boolean sprintSupportEnabled = false;
	private List<Sprint> sprints = new ArrayList<Sprint>();

	public RapidViewBoard(int boardId, String name,
			boolean sprintSupportEnabled) {
		super();
		this.boardId = boardId;
		this.name = name;
		this.sprintSupportEnabled = sprintSupportEnabled;
	}

	public RapidViewBoard() {
		
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSprintSupportEnabled() {
		return sprintSupportEnabled;
	}

	public void setSprintSupportEnabled(boolean sprintSupportEnabled) {
		this.sprintSupportEnabled = sprintSupportEnabled;
	}

	public List<Sprint> getSprints() {
		return sprints;
	}

	public void setSprints(List<Sprint> sprints) {
		this.sprints = sprints;
	}
	
	public void addSprint(Sprint s)
	{
		this.sprints.add(s);
	}

}
