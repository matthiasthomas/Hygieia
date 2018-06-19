package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.AggregateWidget;
import com.capitalone.dashboard.service.AggregateService;

@RestController
@RequestMapping("/aggregate")
public class AggregateController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AggregateController.class);
    private final AggregateService aggregateService;
    
    @Autowired
    public AggregateController(AggregateService aggregateService) {
        this.aggregateService = aggregateService;
    }
    
    @RequestMapping(value = "/aggregatewidgets", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AggregateWidget> getAggregateWidgetByDashboardId(@RequestParam(value = "dashboardId", required = true) ObjectId dashboardId,@RequestParam(value = "offset") String reqOffset){
    	LOGGER.debug("getAggregateWidgetByDashboardId called." + dashboardId);
    	int offset = TimeZone.getDefault().getRawOffset();
    	if(!StringUtils.isEmpty(reqOffset))
    		offset = Integer.parseInt(reqOffset);
    	return ResponseEntity.ok(aggregateService.getAggregateWidgetByDashboardId(dashboardId,offset));
    }
}
