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
import com.capitalone.dashboard.model.FunctionalTestsResults;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.FunctionalTestRepository;
import com.capitalone.dashboard.request.CodeQualityRequest;
import com.capitalone.dashboard.util.ApplicationDBLogger;
import com.capitalone.dashboard.util.HygieiaConstants;

@Service
public class FunctionalTestServiceImpl implements FunctionalTestService {
	private static final Log LOG = LogFactory.getLog(FunctionalTestServiceImpl.class); 
	private final FunctionalTestRepository functionalTestRepository;
	private final ComponentRepository componentRepository;

	@Autowired
	public FunctionalTestServiceImpl(FunctionalTestRepository functionalTestRepository,
			ComponentRepository componentRepository) {
		this.functionalTestRepository = functionalTestRepository;
		this.componentRepository = componentRepository;
	}

	@Override
	public DataResponse<Iterable<FunctionalTestsResults>> getFunctionalTestResults(CodeQualityRequest request) {
		List<FunctionalTestsResults> results = new ArrayList<FunctionalTestsResults>();
		//LOG.info(request.getComponentId());
		try {
			Component component = componentRepository.findOne(request.getComponentId());
			CollectorItem item = component.getCollectorItems().get(CollectorType.FunctionalResults).get(0);

			if (item != null) {
				LOG.info("In get FT Collector Item id: "+item.getId());
				results = functionalTestRepository.getFunctionalTestsResultsByCollectorItemId(item.getId());
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

	@Override
	public DataResponse<Set<String>> getFunctionalTestJobs() {
		Set<String> results = new HashSet<String>();
		try {
			Iterable<FunctionalTestsResults> functionalTests = functionalTestRepository.findAll();
			if(functionalTests.iterator().hasNext()){
				functionalTests.forEach(functionalTest -> results.add(functionalTest.getName()));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			ApplicationDBLogger.log(HygieiaConstants.API,
					"FunctionalTestServiceImpl.getFunctionalTestJobs",
					e.getMessage(), e);

			if(e instanceof HygieiaException){
                throw e;
			}
		}
		return new DataResponse<>(results, new Date().getTime());
	}

}
