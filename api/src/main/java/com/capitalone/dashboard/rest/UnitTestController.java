package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.editors.CaseInsensitiveCollectorTypeEditor;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.UnitTestsResults;
import com.capitalone.dashboard.request.CodeQualityRequest;
import com.capitalone.dashboard.service.UnitTestService;

@RestController
public class UnitTestController {

    private UnitTestService unitTestService;
   
    @Autowired
    public UnitTestController(UnitTestService unitTestService) {
        this.unitTestService = unitTestService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(CollectorType.class, new CaseInsensitiveCollectorTypeEditor());
    }

    @RequestMapping(value = "/unittestresults", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<UnitTestsResults>> unitTestResults(@Valid CodeQualityRequest request) {
    	return unitTestService.getUnitTestResults(request);
    }
}

