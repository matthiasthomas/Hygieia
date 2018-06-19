package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.editors.CaseInsensitiveCollectorTypeEditor;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.IntegrationTestsResults;
import com.capitalone.dashboard.request.CodeQualityRequest;
import com.capitalone.dashboard.service.IntegrationTestService;

@RestController
public class IntegrationTestController {

    private IntegrationTestService integrationTestService;
   
    @Autowired
    public IntegrationTestController(IntegrationTestService integrationTestService) {
        this.integrationTestService = integrationTestService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(CollectorType.class, new CaseInsensitiveCollectorTypeEditor());
    }

    @RequestMapping(value = "/integrationtestresults", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<IntegrationTestsResults>> integrationTestResults(@Valid CodeQualityRequest request) {
    	return integrationTestService.getIntegrationTestResults(request);
    }
    
    @RequestMapping(value = "/integrationtestjobs", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Set<String>> integrationTestJobs() {
    	return integrationTestService.getIntegrationTestJobs();
    }
}
