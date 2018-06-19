package com.capitalone.dashboard.service;

import java.util.Set;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.FunctionalTestsResults;
import com.capitalone.dashboard.request.CodeQualityRequest;

public interface FunctionalTestService {

	DataResponse<Iterable<FunctionalTestsResults>> getFunctionalTestResults(CodeQualityRequest request);

	DataResponse<Set<String>> getFunctionalTestJobs();

}
