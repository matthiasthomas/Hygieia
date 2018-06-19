package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Release;

/**
 * Provides release related services like list of releases, release notes etc
 * 
 * @author JMehta
 *
 */
public interface ReleaseService {

	/**
	 * Provides all releases ordered by latest first
	 * 
	 * @return List of all releases ordered by latest first
	 */
	Iterable<Release> getAllReleases();

	/**
	 * Provides release notes for release of given project.
	 * 
	 * @param projectId
	 *            Id of project for which release notes are sought
	 * @param releaseId
	 *            Id of release for which release notes are sought
	 * @return Provides release notes for given project id and release id.
	 */
	Release getReleaseNotes(long projectId, String releaseId);

}
