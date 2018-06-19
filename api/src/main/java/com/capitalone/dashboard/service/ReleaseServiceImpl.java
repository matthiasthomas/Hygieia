package com.capitalone.dashboard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.Release;
import com.capitalone.dashboard.repository.ReleaseRepository;

@Service
public class ReleaseServiceImpl implements ReleaseService {

	private ReleaseRepository releaseRepository;

	@Autowired
	public ReleaseServiceImpl(ReleaseRepository releaseRepository) {
		this.releaseRepository = releaseRepository;
	}

	public Iterable<Release> getAllReleases() {
		List<Release> releases = releaseRepository
				.findByOrderByReleaseDateDesc();
		return releases;
	}

	public Release getReleaseNotes(long projectId, String releaseId) {
		List<Release> releases = releaseRepository.findByProjectIdAndReleaseId(
				projectId, releaseId);
		if (!releases.isEmpty() && releases.size() == 1) {
			return releases.get(0);
		} else {
			return null;
		}
	}

}
