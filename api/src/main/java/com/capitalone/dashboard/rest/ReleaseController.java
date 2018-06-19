package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.Release;
import com.capitalone.dashboard.service.ReleaseService;

/**
 * Provides REST API for release functionality
 * 
 * @author JMehta
 *
 */
@RestController
@RequestMapping("/unsecured/release")
public class ReleaseController {

	private ReleaseService releaseService;

	@Autowired
	public ReleaseController(ReleaseService releaseService) {
		this.releaseService = releaseService;
	}

	/**
	 * Provides all releases ordered by latest first
	 * 
	 * @return List of all releases ordered by latest first
	 */
	@RequestMapping(path = "/releases", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Iterable<Release>> fetchAllReleases() {
		Iterable<Release> releases = releaseService.getAllReleases();
		return new ResponseEntity<Iterable<Release>>(releases, HttpStatus.OK);
	}

	/**
	 * Provides release notes for release of given project.
	 * 
	 * @param projectId
	 *            Id of project for which release notes are sought
	 * @param releaseId
	 *            Id of release for which release notes are sought
	 * @return Provides release notes for given project id and release id.
	 */
	@RequestMapping(path = "/releaseNotes/{projectId}/{releaseId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Release> fetchReleaseNotes(
			@PathVariable("projectId") long projectId,
			@PathVariable("releaseId") String releaseId) {
		Release release = releaseService.getReleaseNotes(projectId, releaseId);
		return new ResponseEntity<Release>(release, HttpStatus.OK);
	}

}
