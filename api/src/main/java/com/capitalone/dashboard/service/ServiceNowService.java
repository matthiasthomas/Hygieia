package com.capitalone.dashboard.service;

import java.util.List;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.IncidentDetails;
import com.capitalone.dashboard.request.ServiceNowSearchRequest;

public interface ServiceNowService {

    /**
     * Finds all Collectors of a given type.
     *
     * @param collectorType collector type
     * @return Collectors matching the specified type
     */
	List<CollectorItem> assignmentGroupsByClass(String collectorClass);

	DataResponse<Iterable<IncidentDetails>> search(ServiceNowSearchRequest request);
    
}
