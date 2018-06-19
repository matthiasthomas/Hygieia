package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.UnitTestsResults;
import com.capitalone.dashboard.request.CodeQualityRequest;

public interface UnitTestService {

	DataResponse<Iterable<UnitTestsResults>> getUnitTestResults(CodeQualityRequest request);
}
