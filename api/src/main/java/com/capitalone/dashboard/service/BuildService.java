package com.capitalone.dashboard.service;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildAggregateData;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.BuildSearchRequest;

public interface BuildService {

    /**
     * Finds all of the Builds matching the specified request criteria.
     *
     * @param request search criteria
     * @return builds matching criteria
     */
    DataResponse<Iterable<Build>> search(BuildSearchRequest request);

    String create(BuildDataCreateRequest request) throws HygieiaException;

	DataResponse<BuildAggregateData> getAggregateForProductDashboard(
			ObjectId dashboardId);

}
