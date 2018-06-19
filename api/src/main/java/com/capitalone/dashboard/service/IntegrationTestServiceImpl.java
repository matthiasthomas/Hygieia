package com.capitalone.dashboard.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.IntegrationTestsResults;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.IntegrationTestRepository;
import com.capitalone.dashboard.request.CodeQualityRequest;
import com.capitalone.dashboard.util.ApplicationDBLogger;
import com.capitalone.dashboard.util.HygieiaConstants;

@Service
public class IntegrationTestServiceImpl implements IntegrationTestService {
	private static final Log LOG = LogFactory.getLog(IntegrationTestServiceImpl.class); 
	private final IntegrationTestRepository integrationTestRepository;
	private final ComponentRepository componentRepository;

	@Autowired
	public IntegrationTestServiceImpl(IntegrationTestRepository integrationTestRepository,
			ComponentRepository componentRepository) {
		this.integrationTestRepository = integrationTestRepository;
		this.componentRepository = componentRepository;
	}

	@Override
	public DataResponse<Iterable<IntegrationTestsResults>> getIntegrationTestResults(CodeQualityRequest request) {
		List<IntegrationTestsResults> results = new ArrayList<IntegrationTestsResults>();
		//LOG.info(request.getComponentId());
		try {
			Component component = componentRepository.findOne(request.getComponentId());
			CollectorItem item = component.getCollectorItems().get(CollectorType.IntegrationResults).get(0);

			if (item != null) {
				LOG.info("In get Integration test Collector Item id: "+item.getId());
				results = integrationTestRepository.getIntegrationTestsResultsByCollectorItemId(item.getId());
				Collections.sort(results, (o1, o2) -> (Integer.parseInt(o1.getBuildId()) - Integer.parseInt(o2.getBuildId())));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			ApplicationDBLogger.log(HygieiaConstants.API,
					"IntegrationTestServiceImpl.getIntegrationTestResults",
					e.getMessage(), e);
			
			if(e instanceof HygieiaException){
                throw e;
			}
		}
		return new DataResponse<>(results, new Date().getTime());
	}

	@Override
	public DataResponse<Set<String>> getIntegrationTestJobs() {
		Set<String> results = new HashSet<String>();
		try {
			Iterable<IntegrationTestsResults> integrationTests = integrationTestRepository.findAll();
			if(integrationTests.iterator().hasNext()){
				integrationTests.forEach(IntegrationTest -> results.add(IntegrationTest.getName()));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			ApplicationDBLogger.log(HygieiaConstants.API,
					"IntegrationTestServiceImpl.getIntegrationTestJobs",
					e.getMessage(), e);

			if(e instanceof HygieiaException){
                throw e;
			}
		}
		return new DataResponse<>(results, new Date().getTime());
	}

}
