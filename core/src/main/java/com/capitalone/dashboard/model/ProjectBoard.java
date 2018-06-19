/*************************DA-BOARD-LICENSE-START*********************************
 * Copyright 2014 CapitalOne, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************DA-BOARD-LICENSE-END*********************************/

package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.capitalone.dashboard.model.jira.RapidViewBoard;

/**
 * Represents all boards
 */
@Document(collection = "jira_board")
public class ProjectBoard extends BaseModel {
	@Indexed
	private long projectId;
	private String projectName;
	private String source;
	private List<RapidViewBoard> boards = new ArrayList<RapidViewBoard>();

	public ProjectBoard(long projectId, String projectName, String source) {
		super();
		this.projectId = projectId;
		this.projectName = projectName;
		this.source = source;
	}

	public ProjectBoard() {
		// TODO Auto-generated constructor stub
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<RapidViewBoard> getBoards() {
		return boards;
	}

	public void setBoards(List<RapidViewBoard> boards) {
		this.boards = boards;
	}

	public void addBoard(RapidViewBoard board)
	{
		this.boards.add(board);
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((boards == null) ? 0 : boards.hashCode());
		result = prime * result + (int) (projectId ^ (projectId >>> 32));
		result = prime * result
				+ ((projectName == null) ? 0 : projectName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		} else {
			ProjectBoard other = (ProjectBoard) obj;
			if ((boards == null && other.boards != null) 
					|| (boards != null && !boards.equals(other.boards))
					|| projectId != other.projectId
					|| (projectName == null && other.projectName != null)
					|| (projectName != null && !projectName.equals(other.projectName))) {
				return false;
			}
		}
		
		return true;
	}
}
