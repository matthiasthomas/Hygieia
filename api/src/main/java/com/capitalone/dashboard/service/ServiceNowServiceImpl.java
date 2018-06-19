package com.capitalone.dashboard.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.IncidentDetails;
import com.capitalone.dashboard.model.QIncidentDetails;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.IncidentRepository;
import com.capitalone.dashboard.repository.SNAssignmentGroupRepository;
import com.capitalone.dashboard.request.ServiceNowSearchRequest;
import com.mysema.query.BooleanBuilder;

@Service
public class ServiceNowServiceImpl implements ServiceNowService {

    private final SNAssignmentGroupRepository snAssignmentGroupRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final IncidentRepository incidentRepository;

    @Autowired
    public ServiceNowServiceImpl(SNAssignmentGroupRepository snAssignmentGroupRepository,
    		 ComponentRepository componentRepository,
    		 CollectorRepository collectorRepository,
             IncidentRepository incidentRepository
                              ) {
        this.snAssignmentGroupRepository = snAssignmentGroupRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.incidentRepository = incidentRepository;
    }

    @Override
	public List<CollectorItem> assignmentGroupsByClass(String collectorClass) {
		return snAssignmentGroupRepository.findAssignmentGroupsByClass(collectorClass);
	}

    @Override
    public DataResponse<Iterable<IncidentDetails>> search(ServiceNowSearchRequest request) {
        Component component = componentRepository.findOne(request.getComponentId());
        
        // START HYG-178 : Amended to find details based on assignment group selection, if
     	// available.
		CollectorItem item = null;
		if (request.getCollectorItemId() != null) {
			item = component.getCollectorItemForTypeAndId(
					CollectorType.ServiceNow, request.getCollectorItemId());
		} else {
			item = component.getFirstCollectorItemForType(CollectorType.ServiceNow);
		}
		// END HYG-178 : Amended to find details based on assignment group selection, if
		// available.
        
        if (item == null) {
            Iterable<IncidentDetails> results = new ArrayList<>();
            return new DataResponse<>(results, new Date().getTime());
        }

        QIncidentDetails inc = new QIncidentDetails("incidentDetails");
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(inc.collectorItemId.eq(item.getId()));

       if (request.getNumberOfDays() != null) {
            Date endTimeTarget = new DateTime().minusDays(request.getNumberOfDays()).toDate();
             builder.and(inc.createdDate.goe(endTimeTarget.getTime()));
         
        } 
        
       /*if (!request.getState().isEmpty()) {
            builder.and(inc.state.in(request.getState()));
        }*/

        Collector collector = collectorRepository.findOne(item.getCollectorId());

        Iterable<IncidentDetails> result;
      
            result = incidentRepository.findAll(builder.getValue());
        

        return new DataResponse<>(result, collector.getLastExecuted());
    }

}
