package com.capitalone.dashboard.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.UnitTestsResults;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.UnitTestsRepository;
import com.capitalone.dashboard.request.CodeQualityRequest;
import com.capitalone.dashboard.util.ApplicationDBLogger;
import com.capitalone.dashboard.util.HygieiaConstants;

@Service
public class UnitTestServiceImpl implements UnitTestService {
	private static final Log LOG = LogFactory.getLog(UnitTestServiceImpl.class); 
	private final UnitTestsRepository unitTestsRepository;
	private final ComponentRepository componentRepository;

	@Autowired
	public UnitTestServiceImpl(UnitTestsRepository unitTestsRepository,
			ComponentRepository componentRepository) {
		this.unitTestsRepository = unitTestsRepository;
		this.componentRepository = componentRepository;
	}

	@Override
	public DataResponse<Iterable<UnitTestsResults>> getUnitTestResults(CodeQualityRequest request) {
		List<UnitTestsResults> results = new ArrayList<UnitTestsResults>();
		//LOG.info(request.getComponentId());
		try {
			Component component = componentRepository.findOne(request.getComponentId());
			CollectorItem item = component.getCollectorItems().get(CollectorType.UnitTestResults).get(0);

			if (item != null) {
				LOG.info("In get FT Collector Item id: "+item.getId());
				results = unitTestsRepository.getUnitTestsResultsByCollectorItemId(item.getId());
				Collections.sort(results, (o1, o2) -> (Integer.parseInt(o1.getBuildId()) - Integer.parseInt(o2.getBuildId())));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			ApplicationDBLogger.log(HygieiaConstants.API,
					"FunctionalTestServiceImpl.getFunctionalTestResults",
					e.getMessage(), e);
			
			if(e instanceof HygieiaException){
                throw e;
			}
		}
		return new DataResponse<>(results, new Date().getTime());
	}
}
