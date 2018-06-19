package com.capitalone.dashboard.collector;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.CASTProject;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.util.ApplicationDBLogger;
import com.capitalone.dashboard.util.HygieiaConstants;
import com.capitalone.dashboard.util.Supplier;

/**
 * CAST client implementation for getting CAST projects and quality analysis.
 */

@Component
public class DefaultCASTClient implements CASTClient {

	private static final Log LOG = LogFactory.getLog(DefaultCASTClient.class);
	private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");
	private final RestOperations rest;
	private final CASTSettings settings;

	@Autowired
	public DefaultCASTClient(Supplier<RestOperations> restOperationsSupplier, CASTSettings settings) {
		this.rest = restOperationsSupplier.get();
		this.settings = settings;
	}

	@Override
	public List<CASTProject> getCASTProjects(ObjectId collectorId) {
		List<CASTProject> projects = new ArrayList<CASTProject>();
		String name = null;
		try {
			ResponseEntity<String> response = makeRestCall(
					settings.getUrl() + "/rest/" + settings.getProjectListSuffix());
			JSONArray jsonArray = paresAsArray(response);

			for (Object object : jsonArray) {
				JSONObject jsonObject = (JSONObject) object;
				name = str(jsonObject, "name");
				LOG.info("Project Name:" + name);
				String projectUrl = str(jsonObject, "href");

				CASTProject castProject = new CASTProject();
				castProject.setCollectorId(collectorId);
				castProject.setEnabled(false);
				castProject.setDescription(name);
				castProject.setProjectName(name);
				castProject.setProjectUrl(projectUrl);
				projects.add(castProject);
			}
		} catch (MalformedURLException e) {
			ApplicationDBLogger.log(HygieiaConstants.CAST_COLLECTOR, "DefaultCASTClient.getCASTProjects",
					"Exception while fetching cast project data:" + name, e);
			LOG.error("Error while fetching CAST projects data:" + name, e);
		} catch (URISyntaxException e) {
			ApplicationDBLogger.log(HygieiaConstants.CAST_COLLECTOR, "DefaultCASTClient.getCASTProjects",
					"Exception while fetching cast project data:" + name, e);
			LOG.error("Error while fetching CAST projects data:" + name, e);
		} catch (Exception e) {
			ApplicationDBLogger.log(HygieiaConstants.CAST_COLLECTOR, "DefaultCASTClient.getCASTProjects",
					"Exception while fetching cast project data:" + name, e);
			LOG.error("Error while fetching CAST projects data:" + name, e);
		}
		return projects;
	}

	@Override
	public CodeQuality getQualityDetails(CASTProject castProject) {
		String projectName = castProject.getProjectName();
		String castProjectUrl = settings.getUrl() + "/rest/" + castProject.getProjectUrl();
		CodeQuality codeQuality = new CodeQuality();
		codeQuality.setCollectorItemId(castProject.getId());
		codeQuality.setType(CodeQualityType.CAST);
		codeQuality.setName(projectName);
		codeQuality.setTimestamp(System.currentTimeMillis());
		codeQuality.setUrl(settings.getUrl() + "/portal/index.html#app/" + castProject.getProjectUrl());
		double metricMaintainabilityValue = 0.0, metricRiskValue = 0.0;

		Map<String, CodeQualityMetric> matrices = new HashMap<String, CodeQualityMetric>();
		try {
			JSONArray jsonArray = parseAsArray(castProjectUrl
					+ "/results?select=(evolutionSummary)&quality-indicators=(" + settings.getQualityIndicators() + ")",
					"applicationResults");

			if (jsonArray != null) {
				// loop over metrics
				for (Object object : jsonArray) {
					JSONObject jsonObject = (JSONObject) object;
					JSONObject referenceObject = (JSONObject) jsonObject.get("reference");
					String name = str(referenceObject, "name");
					String key = str(referenceObject, "key");
					JSONObject resultJsonObject = (JSONObject) jsonObject.get("result");
					String grade = str(resultJsonObject, "grade");
					CodeQualityMetric metric = new CodeQualityMetric(name);
					if (StringUtils.isEmpty(grade)) {
						grade = "0";
					}
					metric.setValue(grade);
					metric.setFormattedValue(decimalFormat.format(Double.valueOf(grade)));
					matrices.put(metric.getName(), metric);
					// if (resultJsonObject != null) {
					// if (resultJsonObject.containsKey("evolutionSummary")) {
					// JSONObject evolutionSummary = (JSONObject)
					// resultJsonObject.get("evolutionSummary");
					// if (evolutionSummary != null) {
					// addMetricInfo(evolutionSummary, "totalCriticalViolations", matrices);
					// addMetricInfo(evolutionSummary, "addedCriticalViolations", matrices);
					// addMetricInfo(evolutionSummary, "removedCriticalViolations", matrices);
					// }
					// }
					// }

					// Make the risk and maintainabilty calculation
					// Retrieve the added and removed critical violations from tqi
					switch (key) {
					// transferability
					case "60011":
						metricMaintainabilityValue += Double.parseDouble(metric.getValue().toString());
						break;
					// changeability
					case "60012":
						metricMaintainabilityValue += Double.parseDouble(metric.getValue().toString());
						break;
					// robustness
					case "60013":
						metricRiskValue += Double.parseDouble(metric.getValue().toString());
						break;
					// efficiency
					case "60014":
						metricRiskValue += Double.parseDouble(metric.getValue().toString());
						break;
					// security
					case "60016":
						metricRiskValue += Double.parseDouble(metric.getValue().toString());
						break;
					// tqi
					case "60017":
						if (resultJsonObject != null && resultJsonObject.containsKey("evolutionSummary")) {
							// Create the critical violations total, added and removed metrics
							CodeQualityMetric metricAddedCritical = new CodeQualityMetric("critical_added");
							CodeQualityMetric metricRemovedCritical = new CodeQualityMetric("critical_removed");
							CodeQualityMetric metricTotalCritical = new CodeQualityMetric("critical_total");

							String addedCritical = ((JSONObject) resultJsonObject.get("evolutionSummary"))
									.get("addedCriticalViolations").toString();
							String removedCritical = ((JSONObject) resultJsonObject.get("evolutionSummary"))
									.get("removedCriticalViolations").toString();
							String totalCritical = ((JSONObject) resultJsonObject.get("evolutionSummary"))
									.get("totalCriticalViolations").toString();

							metricAddedCritical.setValue(addedCritical);
							matrices.put(metricAddedCritical.getName(), metricAddedCritical);
							metricRemovedCritical.setValue(removedCritical);
							matrices.put(metricRemovedCritical.getName(), metricRemovedCritical);
							metricTotalCritical.setValue(totalCritical);
							matrices.put(metricTotalCritical.getName(), metricTotalCritical);
						}
						break;
					default:
						break;
					}
				}

				CodeQualityMetric metricRisk = new CodeQualityMetric("risk");
				CodeQualityMetric metricMaintainability = new CodeQualityMetric("maintainability");

				metricMaintainabilityValue /= 2;
				metricRiskValue /= 3;

				metricRisk.setValue(metricRiskValue);
				metricMaintainability.setValue(metricMaintainabilityValue);

				matrices.put(metricRisk.getName(), metricRisk);
				matrices.put(metricMaintainability.getName(), metricMaintainability);
			}

			codeQuality.getMetrics().addAll(matrices.values());
		} catch (MalformedURLException e) {
			ApplicationDBLogger.log(HygieiaConstants.CAST_COLLECTOR, "DefaultCASTClient.getQualityDetails",
					"Exception while fetching cast quality details:" + projectName, e);
			LOG.error("Error while fetching cast quality details:" + projectName, e);
		} catch (URISyntaxException e) {
			ApplicationDBLogger.log(HygieiaConstants.CAST_COLLECTOR, "DefaultCASTClient.getQualityDetails",
					"Exception while fetching cast quality details:" + projectName, e);
			LOG.error("Error while fetching cast quality details:" + projectName, e);
		} catch (Exception e) {
			ApplicationDBLogger.log(HygieiaConstants.CAST_COLLECTOR, "DefaultCASTClient.getCASTProjects",
					"Exception while fetching cast project data:" + projectName, e);
			LOG.error("Error while fetching CAST projects data:" + projectName, e);
		}
		return codeQuality;
	}

	private void addMetricInfo(JSONObject value, String key, Map<String, CodeQualityMetric> matrices) {
		CodeQualityMetric metric = new CodeQualityMetric();
		String result = str(value, key);
		metric.setName(key);
		metric.setValue(result);
		matrices.put(metric.getName(), metric);
	}

	protected ResponseEntity<String> makeRestCall(String sUrl) throws MalformedURLException, URISyntaxException {
		LOG.debug("Enter makeRestCall " + sUrl);
		URI thisuri = URI.create(sUrl);
		// Basic Auth only.
		return rest.exchange(thisuri, HttpMethod.GET, new HttpEntity<>(createHeaders(this.settings.getCredentials())),
				String.class);
	}

	protected HttpHeaders createHeaders(final String credentials) {
		String authHeader = "Basic " + credentials;
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, authHeader);
		return headers;
	}

	private JSONArray paresAsArray(ResponseEntity<String> response) {
		String body = "";
		try {
			body = response.getBody();
			return (JSONArray) new JSONParser().parse(body);
		} catch (ParseException pe) {
			ApplicationDBLogger.log(HygieiaConstants.CAST_COLLECTOR, "DefaultCASTClient.paresAsArray",
					"Issues parsing JSON data", pe, body);
			LOG.error(pe.getMessage());
		}
		return new JSONArray();
	}

	private String str(JSONObject json, String key) {
		Object value = json.get(key);
		return value == null ? null : value.toString();
	}

	private JSONArray parseAsArray(String url, String key)
			throws ParseException, MalformedURLException, URISyntaxException {
		ResponseEntity<String> response = makeRestCall(url);
		JSONParser jsonParser = new JSONParser();
		JSONArray jsonArrObject = (JSONArray) jsonParser.parse(response.getBody());
		if (jsonArrObject.size() == 0)
			return null;

		JSONObject jsonObject = (JSONObject) jsonArrObject.get(0);
		LOG.debug(url);
		return (JSONArray) jsonObject.get(key);
	}
}
