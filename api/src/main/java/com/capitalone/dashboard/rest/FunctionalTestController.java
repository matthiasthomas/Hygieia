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
import com.capitalone.dashboard.model.FunctionalTestsResults;
import com.capitalone.dashboard.request.CodeQualityRequest;
import com.capitalone.dashboard.service.FunctionalTestService;

@RestController
public class FunctionalTestController {

    private FunctionalTestService functionalTestService;
   
    @Autowired
    public FunctionalTestController(FunctionalTestService functionalTestService) {
        this.functionalTestService = functionalTestService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(CollectorType.class, new CaseInsensitiveCollectorTypeEditor());
    }

    @RequestMapping(value = "/functionaltestresults", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<FunctionalTestsResults>> functionalTestResults(@Valid CodeQualityRequest request) {
    	return functionalTestService.getFunctionalTestResults(request);
    }
    
    @RequestMapping(value = "/functionaltestjobs", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Set<String>> functionalTestJobs() {
    	return functionalTestService.getFunctionalTestJobs();
    }
}

