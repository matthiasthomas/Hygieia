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
	public CodeQuality currentCodeQuality(CastProject project) {
		String qualityIndicatorsUrl = "?quality-indicators=(" + ID_TRANSFERABILITY + "," + ID_CHANGEABILITY + ","
				+ ID_ROBUSTNESS + "," + ID_EFFICIENCY + "," + ID_SECURITY + "," + ID_TQI + ")";
		String url = project.getInstanceUrl() + URL_CAST_APPLICATION + "/" + project.getApplicationId() + "/results"
				+ qualityIndicatorsUrl;
		try {

			LOG.info("url: " + url);

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

			String[] metrics = { "Transferability", "Changeability", "Robustness", "Efficiency", "Security",
					"Total Quality Index" };

			JSONArray applicationResults = (JSONArray) jsonObject.get("applicationResults");
			CodeQualityMetric metric;
			JSONObject resultJson;
			double metricMaintainabilityValue = 0.0, metricRiskValue = 0.0;
			LOG.info("metrics.length: " + metrics.length);
			for (int i = 0; i < metrics.length && applicationResults.size() >= metrics.length; i++) {
				metric = new CodeQualityMetric(metrics[i]);
				resultJson = (JSONObject) applicationResults.get(i);
				metric.setValue(((JSONObject) resultJson.get("result")).get("grade").toString());
				codeQuality.getMetrics().add(metric);

				switch (metrics[i]) {
				case "Transferability":
					metricMaintainabilityValue += Double.parseDouble(metric.getValue().toString());
					break;
				case "Changeability":
					metricMaintainabilityValue += Double.parseDouble(metric.getValue().toString());
					break;
				case "Robustness":
					metricRiskValue += Double.parseDouble(metric.getValue().toString());
					break;
				case "Efficiency":
					metricRiskValue += Double.parseDouble(metric.getValue().toString());
					break;
				case "Security":
					metricRiskValue += Double.parseDouble(metric.getValue().toString());
					break;
				default:
					break;
				}
			}

			LOG.info("APRES LA BOUCLE ?!!");

			CodeQualityMetric metricRisk = new CodeQualityMetric("Risk Indicator");
			CodeQualityMetric metricMaintainability = new CodeQualityMetric("Maintainability Indicator");

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
