package com.capitalone.dashboard.service;

import java.util.Map;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.GitRequestRequest;

public interface GitKPIService {

    DataResponse<Map<String,Object>> fetchKpi(GitRequestRequest request);
}
