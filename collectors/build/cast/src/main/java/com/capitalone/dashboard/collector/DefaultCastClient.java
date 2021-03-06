package com.capitalone.dashboard.collector;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.capitalone.dashboard.model.CastProject;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityMetricStatus;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.util.Supplier;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

@Component
public class DefaultCastClient implements CastClient {
	private static final Log LOG = LogFactory.getLog(DefaultCastClient.class);

	private static final String STATUS_WARN = "WARN";
	private static final String STATUS_ALERT = "ALERT";

	private final RestOperations rest;
	private final HttpEntity<String> httpHeaders;

	private static final String HREF = "href";
	private static final String NAME = "name";

	private static final String URL_CAST_APPLICATION = "/AAD/applications/";

	private static final String ID_TRANSFERABILITY = "60011";
	private static final String ID_CHANGEABILITY = "60012";
	private static final String ID_ROBUSTNESS = "60013";
	private static final String ID_EFFICIENCY = "60014";
	private static final String ID_SECURITY = "60016";
	private static final String ID_TQI = "60017";

	@Autowired
	public DefaultCastClient(Supplier<RestOperations> restOperationsSupplier, CastSettings settings) {
		this.httpHeaders = new HttpEntity<>(this.createHeaders(settings.getUsername(), settings.getPassword()));
		this.rest = restOperationsSupplier.get();
	}

	@Override
	public List<CastProject> getApplications(String instanceUrl) {
		List<CastProject> projects = new ArrayList<>();
		String url = instanceUrl + URL_CAST_APPLICATION;

		try {

			for (Object obj : parseAsArray(url)) {
				JSONObject prjData = (JSONObject) obj;

				CastProject project = new CastProject();
				String[] splitId = str(prjData, HREF).split("/");
				String splitIdStr = splitId[splitId.length - 1];
				project.setInstanceUrl(instanceUrl);
				project.setApplicationId(splitIdStr);
				project.setApplicationName(str(prjData, NAME));
				projects.add(project);
			}

		} catch (ParseException e) {
			LOG.error("Could not parse response from: " + url, e);
		} catch (RestClientException rce) {
			LOG.error(rce);
		}

		return projects;
	}

	@Override
	@SuppressWarnings("PMD")
	public CodeQuality currentCodeQuality(CastProject project) {
		String qualityIndicatorsUrl = "?select=(evolutionSummary)&quality-indicators=(" + ID_TRANSFERABILITY + ","
				+ ID_CHANGEABILITY + "," + ID_ROBUSTNESS + "," + ID_EFFICIENCY + "," + ID_SECURITY + "," + ID_TQI + ")";
		String url = project.getInstanceUrl() + URL_CAST_APPLICATION + "/" + project.getApplicationId() + "/results"
				+ qualityIndicatorsUrl;
		try {

			ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, this.httpHeaders, String.class);
			JSONParser jsonParser = new JSONParser();
			JSONArray jsonArray = (JSONArray) jsonParser.parse(response.getBody());
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);

			CodeQuality codeQuality = new CodeQuality();
			codeQuality.setType(CodeQualityType.StaticAnalysis);
			codeQuality.setName(project.getApplicationName());
			codeQuality.setUrl(project.getInstanceUrl() + '/' + URL_CAST_APPLICATION + project.getApplicationId());

			codeQuality.setTimestamp(convertToLong(((JSONObject) jsonObject.get("date")).get("time")));

			// Init metrics

			String[] metrics = { "transfer", "change", "robustness", "efficiency", "security", "tqi" };

			JSONArray applicationResults = (JSONArray) jsonObject.get("applicationResults");
			CodeQualityMetric metric;
			JSONObject resultJson;
			double metricMaintainabilityValue = 0.0, metricRiskValue = 0.0;

			for (int i = 0; i < metrics.length && applicationResults.size() >= metrics.length; i++) {
				metric = new CodeQualityMetric(metrics[i]);
				resultJson = (JSONObject) applicationResults.get(i);
				String value;
				if (((JSONObject) resultJson.get("result")).get("grade") != null) {
					value = ((JSONObject) resultJson.get("result")).get("grade").toString();
				} else {
					value = "";
				}
				LOG.info(value);
				metric.setValue(value);
				codeQuality.getMetrics().add(metric);

				// Make the risk and maintainabilty calculation
				// Retrieve the added and removed critical violations from tqi
				switch (metrics[i]) {
				case "transfer":
					metricMaintainabilityValue += Double.parseDouble(metric.getValue().toString());
					break;
				case "change":
					metricMaintainabilityValue += Double.parseDouble(metric.getValue().toString());
					break;
				case "robustness":
					metricRiskValue += Double.parseDouble(metric.getValue().toString());
					break;
				case "efficiency":
					metricRiskValue += Double.parseDouble(metric.getValue().toString());
					break;
				case "security":
					metricRiskValue += Double.parseDouble(metric.getValue().toString());
					break;
				case "tqi":
					// Create the critical violations total, added and removed metrics
					CodeQualityMetric metricAddedCritical = new CodeQualityMetric("critical_added");
					CodeQualityMetric metricRemovedCritical = new CodeQualityMetric("critical_removed");
					CodeQualityMetric metricTotalCritical = new CodeQualityMetric("critical_total");

					String addedCritical = ((JSONObject) ((JSONObject) resultJson.get("result"))
							.get("evolutionSummary")).get("addedCriticalViolations").toString();
					String removedCritical = ((JSONObject) ((JSONObject) resultJson.get("result"))
							.get("evolutionSummary")).get("removedCriticalViolations").toString();
					String totalCritical = ((JSONObject) ((JSONObject) resultJson.get("result"))
							.get("evolutionSummary")).get("totalCriticalViolations").toString();

					metricAddedCritical.setValue(addedCritical);
					metricRemovedCritical.setValue(removedCritical);
					metricTotalCritical.setValue(totalCritical);

					codeQuality.getMetrics().add(metricAddedCritical);
					codeQuality.getMetrics().add(metricRemovedCritical);
					codeQuality.getMetrics().add(metricTotalCritical);
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

			// Add metrics to code quality
			codeQuality.getMetrics().add(metricRisk);
			codeQuality.getMetrics().add(metricMaintainability);

			return codeQuality;

		} catch (ParseException e) {
			LOG.error("Could not parse response from: " + url, e);
		} catch (RestClientException rce) {
			LOG.error("Rest Client Exception: " + url + ":" + rce.getMessage());
		} catch (NumberFormatException nfe) {
			LOG.error("Could not convert value to Double: " + nfe.getMessage());
		}

		return null;
	}

	public static Long convertToLong(Object o) {
		String stringToConvert = String.valueOf(o);
		Long convertedLong = Long.parseLong(stringToConvert);
		return convertedLong;

	}

	private JSONArray parseAsArray(String url) throws ParseException {
		ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, this.httpHeaders, String.class);
		return (JSONArray) new JSONParser().parse(response.getBody());
	}

	@SuppressWarnings("unused")
	private JSONArray parseAsArray(String url, String key) throws ParseException {
		ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, this.httpHeaders, String.class);
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
		LOG.debug(url);
		return (JSONArray) jsonObject.get(key);
	}

	private String str(JSONObject json, String key) {
		Object obj = json.get(key);
		return obj == null ? null : obj.toString();
	}

	@SuppressWarnings("unused")
	private Integer integer(JSONObject json, String key) {
		Object obj = json.get(key);
		return obj == null ? null : (Integer) obj;
	}

	@SuppressWarnings("unused")
	private BigDecimal decimal(JSONObject json, String key) {
		Object obj = json.get(key);
		return obj == null ? null : new BigDecimal(obj.toString());
	}

	@SuppressWarnings("unused")
	private Boolean bool(JSONObject json, String key) {
		Object obj = json.get(key);
		return obj == null ? null : Boolean.valueOf(obj.toString());
	}

	@SuppressWarnings("unused")
	private CodeQualityMetricStatus metricStatus(String status) {
		if (StringUtils.isBlank(status)) {
			return CodeQualityMetricStatus.Ok;
		}

		switch (status) {
		case STATUS_WARN:
			return CodeQualityMetricStatus.Warning;
		case STATUS_ALERT:
			return CodeQualityMetricStatus.Alert;
		default:
			return CodeQualityMetricStatus.Ok;
		}
	}

	private HttpHeaders createHeaders(String username, String password) {
		HttpHeaders headers = new HttpHeaders();
		if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
			String auth = username + ":" + password;
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
			String authHeader = "Basic " + new String(encodedAuth);
			headers.set("Authorization", authHeader);
		}
		return headers;
	}
}
