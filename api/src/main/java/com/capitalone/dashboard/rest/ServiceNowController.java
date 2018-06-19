package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.editors.CaseInsensitiveCollectorTypeEditor;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.IncidentDetails;
import com.capitalone.dashboard.request.ServiceNowSearchRequest;
import com.capitalone.dashboard.service.ServiceNowService;

@RestController
public class ServiceNowController {

    private ServiceNowService serviceNowService;
   
    private static final String CLASS_BASE_PATH = "com.capitalone.dashboard.model.";


    @Autowired
    public ServiceNowController(ServiceNowService serviceNowService) {
        this.serviceNowService = serviceNowService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(CollectorType.class, new CaseInsensitiveCollectorTypeEditor());
    }

    @RequestMapping(value = "/collector/assignment/group/{collectorClass}", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<CollectorItem> assignmentGroupsByClass(@PathVariable String collectorClass) {
        return serviceNowService.assignmentGroupsByClass(CLASS_BASE_PATH + collectorClass);
    }
    @RequestMapping(value = "/incidents", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<IncidentDetails>> incidents(@Valid ServiceNowSearchRequest request) {
        return serviceNowService.search(request);
    }
}

