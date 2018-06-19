package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.ProjectBoard;
import com.capitalone.dashboard.model.SprintEstimate;
import com.capitalone.dashboard.model.VelocityData;
import com.capitalone.dashboard.service.FeatureService;
import com.capitalone.dashboard.util.RestAPISupplier;

/**
 * REST service managing all requests to the feature repository.
 *
 * @author KFK884
 *
 */
@RestController
public class FeatureController {
	private FeatureService featureService = null;
	private static final Logger LOGGER = LoggerFactory //NOPMD
			.getLogger(FeatureController.class); 

	@Value("${jiraURL}")
	// Injected from application.properties
	private String jiraURL;

	@Autowired
	public FeatureController(FeatureService featureService) {
		this.featureService = featureService;
	}

	/**
	 * REST endpoint for retrieving all features for a given sprint and team
	 * (the sprint is derived)
	 *
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A data response list of type Feature containing all features for
	 *         the given team and current sprint
	 */
	@RequestMapping(value = "/feature/{teamId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public DataResponse<List<Feature>> relevantStories(
			@RequestParam(value = "projectId", required = true) String projectId,
			@RequestParam(value = "agileType", required = false) Optional<String> agileType,
			@RequestParam(value = "component", required = true) String cId,
			@PathVariable String teamId) {
		ObjectId componentId = new ObjectId(cId);
		return this.featureService.getRelevantStories(componentId, teamId,
				projectId, agileType);
	}

	/**
	 * REST endpoint for retrieving all features for a given sprint and team
	 * (the sprint is derived)
	 *
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A data response list of type Feature containing all features for
	 *         the given team and current sprint
	 */
	@RequestMapping(value = "/feature", method = GET, produces = APPLICATION_JSON_VALUE)
	public DataResponse<List<Feature>> story(
			@RequestParam(value = "component", required = true) String cId,
			@RequestParam(value = "number", required = true) String storyNumber) {
		ObjectId componentId = new ObjectId(cId);
		return this.featureService.getStory(componentId, storyNumber);
	}

	/**
	 * REST endpoint for retrieving the current sprint detail for a team
	 *
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A response list of type Feature containing the done estimate of
	 *         current features
	 */
	@RequestMapping(value = "/iteration/{teamId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public DataResponse<List<Feature>> currentSprintDetail(
			@RequestParam(value = "projectId", required = true) String projectId,
			@RequestParam(value = "agileType", required = false) Optional<String> agileType,
			@RequestParam(value = "component", required = true) String cId,
			@PathVariable String teamId) {
		ObjectId componentId = new ObjectId(cId);
		return this.featureService.getCurrentSprintDetail(componentId, teamId,
				projectId, agileType);
	}

	/**
	 * REST endpoint for retrieving only the unique super features for a given
	 * team and sprint and their related estimates
	 *
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A response list of type Feature containing the unique features
	 *         plus their sub features' estimates associated to the current
	 *         sprint and team
	 */
	@RequestMapping(value = "/feature/estimates/super/{teamId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public DataResponse<List<Feature>> featureEpics(
			@RequestParam(value = "projectId", required = true) String projectId,
			@RequestParam(value = "agileType", required = false) Optional<String> agileType,
			@RequestParam(value = "estimateMetricType", required = false) Optional<String> estimateMetricType,
			@RequestParam(value = "component", required = true) String cId,
			@PathVariable String teamId) {
		ObjectId componentId = new ObjectId(cId);
		return this.featureService.getFeatureEpicEstimates(componentId, teamId,
				projectId, agileType, estimateMetricType);
	}

	/**
	 * REST endpoint for retrieving the current sprint estimates for a team
	 *
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return
	 */
	@RequestMapping(value = "/feature/estimates/aggregatedsprints/{teamId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public DataResponse<SprintEstimate> featureAggregatedSprintEstimates(
			@RequestParam(value = "projectId", required = true) String projectId,
			@RequestParam(value = "agileType", required = false) Optional<String> agileType,
			@RequestParam(value = "estimateMetricType", required = false) Optional<String> estimateMetricType,
			@RequestParam(value = "component", required = true) String cId,
			@PathVariable String teamId) {
		ObjectId componentId = new ObjectId(cId);
		return this.featureService.getAggregatedSprintEstimates(componentId,
				teamId, projectId, agileType, estimateMetricType);
	}

	/**
	 * REST endpoint for retrieving the current total estimate for a team and
	 * sprint
	 *
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A response list of type Feature containing the total estimate of
	 *         current features
	 */
	@RequestMapping(value = "/feature/estimates/total/{teamId}", method = GET, produces = APPLICATION_JSON_VALUE)
	@Deprecated
	public DataResponse<List<Feature>> featureTotalEstimate(
			@RequestParam(value = "agileType", required = false) Optional<String> agileType,
			@RequestParam(value = "estimateMetricType", required = false) Optional<String> estimateMetricType,
			@RequestParam(value = "component", required = true) String cId,
			@PathVariable String teamId) {
		ObjectId componentId = new ObjectId(cId);
		return this.featureService.getTotalEstimate(componentId, teamId,
				agileType, estimateMetricType);
	}

	/**
	 * REST endpoint for retrieving the current in-progress estimate for a team
	 * and sprint
	 *
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A response list of type Feature containing the in-progress
	 *         estimate of current features
	 */
	@RequestMapping(value = "/feature/estimates/wip/{teamId}", method = GET, produces = APPLICATION_JSON_VALUE)
	@Deprecated
	public DataResponse<List<Feature>> featureInProgressEstimate(
			@RequestParam(value = "agileType", required = false) Optional<String> agileType,
			@RequestParam(value = "estimateMetricType", required = false) Optional<String> estimateMetricType,
			@RequestParam(value = "component", required = true) String cId,
			@PathVariable String teamId) {
		ObjectId componentId = new ObjectId(cId);
		return this.featureService.getInProgressEstimate(componentId, teamId,
				agileType, estimateMetricType);
	}

	/**
	 * REST endpoint for retrieving the current done estimate for a team and
	 * sprint
	 *
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A response list of type Feature containing the done estimate of
	 *         current features
	 */
	@RequestMapping(value = "/feature/estimates/done/{teamId}", method = GET, produces = APPLICATION_JSON_VALUE)
	@Deprecated
	public DataResponse<List<Feature>> featureDoneEstimate(
			@RequestParam(value = "agileType", required = false) Optional<String> agileType,
			@RequestParam(value = "estimateMetricType", required = false) Optional<String> estimateMetricType,
			@RequestParam(value = "component", required = true) String cId,
			@PathVariable String teamId) {
		ObjectId componentId = new ObjectId(cId);
		return this.featureService.getDoneEstimate(componentId, teamId,
				agileType, estimateMetricType);
	}

	/**
	 * REST endpoint for retrieving the current done estimate for a team and
	 * velociy
	 *
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A response list of type Feature containing the done estimate of
	 *         current features
	 */
	// This API is not used, we may need to remove it
	@RequestMapping(value = "/feature/velocity/{viewId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public JSONObject featureVelocity(@PathVariable String viewId) {
		String jiraVelocityURL = jiraURL
				+ "/rest/greenhopper/1.0/rapid/charts/velocity.json?rapidViewId="
				+ viewId;
		// LOGGER.info("JIRA Velocity URL:" + jiraVelocityURL);
		RestAPISupplier supplier = new RestAPISupplier();
		RestTemplate restTemplate = (RestTemplate) supplier.get();
		HttpHeaders headers = new HttpHeaders();
		headers.add("user-agent",
				"Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
		headers.add("Authorization",
				"Basic QW1pdGFfS2VzYWlAc3BlLnNvbnkuY29tOldpbmRvd3NEYXNoLTJQYXNz");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
				jiraVelocityURL, HttpMethod.GET, entity, JSONObject.class);
		return responseEntity.getBody();
	}

	/**
	 * REST endpoint for retrieving the board velocity for a team and sprint
	 *
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A response list of type Feature containing the done estimate of
	 *         current features
	 */
	@RequestMapping(value = "/feature/velocity/project/{projectId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public DataResponse<Map<String, VelocityData>> featureprojectVeolcity(
			@PathVariable String projectId) {
		return this.featureService.getBoardVelocityDetail(projectId);
	}

	/**
	 * REST endpoint for retrieving the board velocity for a team and sprint
	 *
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A response list of type Feature containing the done estimate of
	 *         current features
	 */
	@RequestMapping(value = "/feature/velocity/{projectId}/{boardId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public DataResponse<ProjectBoard> featureProjectBoardVelocity(
			@PathVariable String projectId, @PathVariable String boardId) {
		return this.featureService.getProjectBoardVelocityDetail(projectId,
				boardId);
	}

}
