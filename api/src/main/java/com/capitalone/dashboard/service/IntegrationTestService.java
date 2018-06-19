package com.capitalone.dashboard.service;

import java.util.Set;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.IntegrationTestsResults;
import com.capitalone.dashboard.request.CodeQualityRequest;

public interface IntegrationTestService {

	DataResponse<Iterable<IntegrationTestsResults>> getIntegrationTestResults(CodeQualityRequest request);

	DataResponse<Set<String>> getIntegrationTestJobs();

}
