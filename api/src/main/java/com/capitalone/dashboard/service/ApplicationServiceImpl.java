package com.capitalone.dashboard.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CollItemCfgHist;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FunctionalTestsResults;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.IncidentDetails;
import com.mongodb.WriteResult;

@Service
public class ApplicationServiceImpl implements ApplicationService {
	private static final Log LOG = LogFactory
			.getLog(ApplicationServiceImpl.class);
	private final MongoOperations operations;

	@Autowired
	public ApplicationServiceImpl(MongoOperations operations) {
		this.operations = operations;
	}

	@Override
	public List<Map<String, String>> cleanData(int dataRetentionPeriodMonths) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String currentISODateTime = getISODateTime(dataRetentionPeriodMonths);
		MutableDateTime dateTime = new MutableDateTime();
		dateTime.addMonths(dataRetentionPeriodMonths * -1);

		// GITHUB Collector
		BasicQuery query = new BasicQuery("{scmCommitTimestamp : {$lt : "
				+ dateTime.getMillis() + "}}");
		list.add(processQuery(query, Commit.class, "commits", "GitHub"));

		query = new BasicQuery("{scmCommitTimestamp : {$lt : "
				+ dateTime.getMillis() + "}}");
		list.add(processQuery(query, GitRequest.class, "gitrequests", "GitHub"));

		// Build Collector
		query = new BasicQuery("{timestamp : {$lt : " + dateTime.getMillis()
				+ "}}");
		list.add(processQuery(query, CollItemCfgHist.class, "collitem_cfghist",
				"Build"));

		query = new BasicQuery("{timestamp : {$lt : " + dateTime.getMillis()
				+ "}}");
		list.add(processQuery(query, Build.class, "builds", "Build"));

		// Jenkins/Sonar Code Quality
		query = new BasicQuery("{timestamp : {$lt : " + dateTime.getMillis()
				+ "}}");
		list.add(processQuery(query, CodeQuality.class, "code_quality",
				"Jenkins/Sonar Code Quality"));

		// Functional Tests Collector
		query = new BasicQuery("{timestamp : {$lt : " + dateTime.getMillis()
				+ "}}");
		list.add(processQuery(query, FunctionalTestsResults.class,
				"functional_tests", "Functional Tests Collector"));

		// Service Now
		query = new BasicQuery("{state : 'Closed' ,closedDate : {$lt : "
				+ dateTime.getMillis() + "}}");
		list.add(processQuery(query, IncidentDetails.class, "incidents",
				"Service Now Collector"));

		// Feature
		query = new BasicQuery(
				"{sStatus : 'Done',$or : [  {changeDate : {$lt :'"
						+ currentISODateTime
						+ "'},$or : [{'sSprintID' : {$exists : false}} ,{sSprintID : {$eq : null}},{sSprintID : {$eq : ''}}]} , {'sSprintID' : {$exists : true} , sSprintID : {$ne : null},sSprintID : {$ne : ''},sSprintEndDate : {$lt :'"
						+ currentISODateTime + "'} }] }");
		list.add(processQuery(query, Feature.class, "feature",
				"JIRA/Pivotal Collector"));

		return list;
	}

	private String getISODateTime(int dataRetentionPeriodMonths) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MONTH, dataRetentionPeriodMonths * -1);
		return DatatypeConverter.printDateTime(cal);
	}

	private Map<String, String> processQuery(BasicQuery query, Class cls,
			String collection, String collector) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("name", collection);
		data.put("collector", collector);
		data.put("count_before_delete",
				String.valueOf(operations.count(new BasicQuery("{}"), cls)));

		WriteResult result = operations.remove(query, cls);
		data.put("deleted_count", String.valueOf(result.getN()));

		LOG.info("Query for removal :" + query.toString());
		return data;
	}
}
