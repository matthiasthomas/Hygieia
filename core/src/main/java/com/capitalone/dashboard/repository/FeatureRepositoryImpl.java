package com.capitalone.dashboard.repository;

import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

import com.capitalone.dashboard.model.Feature; 
import com.capitalone.dashboard.util.ApplicationDBLogger;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.capitalone.dashboard.util.HygieiaConstants;

/**
 * Repository for {@link FeatureCollector} with custom methods implementation.
 */
public class FeatureRepositoryImpl implements FeatureRepositoryCustom {
	private static final Log LOG = LogFactory.getLog(FeatureRepositoryImpl.class);
	
    @Autowired
    private MongoOperations operations;
    
    @Override
    @SuppressWarnings("PMD.NPathComplexity")
    public List<Feature> findByActiveEndingSprints(String sTeamId, String sProjectId, ObjectId collectorId, String currentISODateTime, boolean minimal,String featureTool) {
        return searchFeatures(sTeamId,sProjectId,collectorId,currentISODateTime,minimal,featureTool,true);
    }
    
    @Override
    @SuppressWarnings("PMD.NPathComplexity")
    public List<Feature> searchFeatures(String sTeamId, String sProjectId, ObjectId collectorId, String currentISODateTime, boolean minimal,String featureTool,boolean filterByActive) {
    	boolean dateTimeValid = true;
        try {
			//Avoid unnecessary exception logs
			if(currentISODateTime!=null && !currentISODateTime.equals("")) {
				DatatypeConverter.parseDateTime(currentISODateTime);
			} else {
				dateTimeValid = false;
			}
        } catch (IllegalArgumentException e) {
			ApplicationDBLogger.log(HygieiaConstants.JIRA_COLLECTOR,
					"FeatureRepositoryImpl.findByActiveEndingSprints", e.getMessage(),
					e);
            // invalid datetime string
            dateTimeValid = false;
        }
        
        String dateQuery = "{'sSprintEndDate' : {$gte : '" + currentISODateTime + "'}},";
        //if(!FeatureCollectorConstants.PIVOTAL.equals(featureTool))
        //dateQuery = "{'sSprintEndDate' : {$gte : '" + currentISODateTime + "'}},";

        String activeQuery = "";
        if(filterByActive)
        	activeQuery = "{'sSprintAssetState': { $regex: '^active$', $options: 'i' }},";
        
        String queryStr = dateTimeValid
                ? "{'isDeleted' : 'False', $and : [{'sSprintID' : {$ne : null}}, {'sSprintID' : {$ne : ''}}, "+ activeQuery + dateQuery+" {'sSprintEndDate' : {$lt : '9999-12-31T59:59:59.999999'}}] }, $orderby: { 'sStatus' :-1 }" 
                : "{'isDeleted' : 'False', $and : [{'sSprintID' : {$ne : null}}, {'sSprintID' : {$ne : ''}}, "+ activeQuery + " {'sSprintEndDate' : {$lt : '9999-12-31T59:59:59.999999'}}] }, $orderby: { 'sStatus' :-1 }";
        BasicQuery query = null;
        if (minimal) {
            query = new BasicQuery(queryStr,
                    "{'sStatus': 1, 'sNumber': 1, 'sName': 1, 'changeDate': 1, 'sUrl': 1, 'sSprintID': 1, 'sSprintName': 1, 'sSprintUrl': 1, 'sSprintBeginDate': 1, 'sSprintEndDate': 1, 'sEpicID' : 1,'sEpicNumber' : 1, 'sEpicName' : 1, 'sEpicUrl' : 1, 'sEstimate': 1, 'sEstimateTime': 1}");
        } else {
            query = new BasicQuery(queryStr);
        }
        if (collectorId != null) {
            query.addCriteria(Criteria.where("collectorId").is(collectorId));
        }
        if (!StringUtils.isEmpty(sTeamId) && !FeatureCollectorConstants.TEAM_ID_ANY.equalsIgnoreCase(sTeamId)) {
            query.addCriteria(Criteria.where("sTeamID").is(sTeamId));
        }
        if (!StringUtils.isEmpty(sProjectId) && !FeatureCollectorConstants.PROJECT_ID_ANY.equalsIgnoreCase(sProjectId)) {
            query.addCriteria(Criteria.where("sProjectID").is(sProjectId));
        }
        //if (!FeatureCollectorConstants.PIVOTAL.equals(featureTool) && dateTimeValid) {
        if (dateTimeValid) {
        query.addCriteria(Criteria.where("sSprintBeginDate").lte(currentISODateTime));
        }
        
        LOG.info("findByActiveEndingSprints :: featureTool  :: " + featureTool  + " ::  query :: " + query);
        
        return operations.find(query, Feature.class);
    }
    
    
    @Override
    public List<Feature> findByUnendingSprints(String sTeamId, String sProjectId, ObjectId collectorId, boolean minimal) {
        BasicQuery query = null;
        if (minimal) {
            query = new BasicQuery("{'isDeleted' : 'False', $and : [{'sSprintID' : {$ne : null}} , {'sSprintID' : {$ne : \"\"}} , {'sSprintAssetState': { $regex: '^active$', $options: 'i' } } , { $or : [{'sSprintEndDate' : {$eq : null}} , {'sSprintEndDate' : {$eq : ''}} , {'sSprintEndDate' : {$gte : '9999-12-31T59:59:59.999999'}}] } ] }, $orderby: { 'sStatus' :-1 }",
                    "{'sStatus': 1, 'sNumber': 1, 'sName': 1, 'changeDate': 1, 'sUrl': 1, 'sSprintID': 1, 'sSprintName': 1, 'sSprintUrl': 1, 'sSprintBeginDate': 1, 'sSprintEndDate': 1, 'sEpicID' : 1,'sEpicNumber' : 1, 'sEpicName' : 1, 'sEpicUrl' : 1, 'sEstimate': 1, 'sEstimateTime': 1}");
        } else {
            query = new BasicQuery("{'isDeleted' : 'False', $and : [{'sSprintID' : {$ne : null}} , {'sSprintID' : {$ne : \"\"}} , {'sSprintAssetState': { $regex: '^active$', $options: 'i' } } , { $or : [{'sSprintEndDate' : {$eq : null}} , {'sSprintEndDate' : {$eq : ''}} , {'sSprintEndDate' : {$gte : '9999-12-31T59:59:59.999999'}}] } ] }, $orderby: { 'sStatus' :-1 }");
        }
        if (collectorId != null) {
            query.addCriteria(Criteria.where("collectorId").is(collectorId));
        }
        if (!StringUtils.isEmpty(sTeamId) && !FeatureCollectorConstants.TEAM_ID_ANY.equalsIgnoreCase(sTeamId)) {
            query.addCriteria(Criteria.where("sTeamID").is(sTeamId));
        }
        if (!StringUtils.isEmpty(sProjectId) && !FeatureCollectorConstants.PROJECT_ID_ANY.equalsIgnoreCase(sProjectId)) {
            query.addCriteria(Criteria.where("sProjectID").is(sProjectId));
        }
        
        return operations.find(query, Feature.class);
    }

    @Override
    public List<Feature> findByNullSprints(String sTeamId, String sProjectId, ObjectId collectorId, boolean minimal) {
        BasicQuery query = null;
        if (minimal) {
            query = new BasicQuery("{'isDeleted' : 'False', $or : [{'sSprintID' : {$eq : null}}, {'sSprintID' : {$eq : \"\"}}] }, $orderby: { 'sStatus' :-1 }",
                    "{'sStatus': 1, 'sNumber': 1, 'sName': 1, 'changeDate': 1, 'sUrl': 1, 'sSprintID': 1, 'sSprintName': 1, 'sSprintUrl': 1, 'sSprintBeginDate': 1, 'sSprintEndDate': 1, 'sEpicID' : 1,'sEpicNumber' : 1, 'sEpicName' : 1, 'sEpicUrl' : 1, 'sEstimate': 1, 'sEstimateTime': 1}");
        } else {
            query = new BasicQuery("{'isDeleted' : 'False', $or : [{'sSprintID' : {$eq : null}}, {'sSprintID' : {$eq : \"\"}}] }, $orderby: { 'sStatus' :-1 }");
        }
        if (collectorId != null) {
            query.addCriteria(Criteria.where("collectorId").is(collectorId));
        }
        if (!StringUtils.isEmpty(sTeamId) && !FeatureCollectorConstants.TEAM_ID_ANY.equalsIgnoreCase(sTeamId)) {
            query.addCriteria(Criteria.where("sTeamID").is(sTeamId));
        }
        if (!StringUtils.isEmpty(sProjectId) && !FeatureCollectorConstants.PROJECT_ID_ANY.equalsIgnoreCase(sProjectId)) {
            query.addCriteria(Criteria.where("sProjectID").is(sProjectId));
        }
        
        return operations.find(query, Feature.class);
    }
    
    @Override
    public int deleteByCollector(ObjectId collectorId) {
        BasicQuery query = new BasicQuery("{}");
        query.addCriteria(Criteria.where("collectorId").is(collectorId));
        
        return operations.remove(query, Feature.class).getN();
    }
    
}
