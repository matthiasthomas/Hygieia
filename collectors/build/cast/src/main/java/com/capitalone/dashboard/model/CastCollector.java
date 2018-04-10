/**
 * 
 */
package com.capitalone.dashboard.model;

/**
 * @author KKH
 *
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class CastCollector extends Collector{

	/**
	 * 
	 */
    private List<String> castServers = new ArrayList<String>();
    private List<Double> castVersions = new ArrayList<Double>();
    private List<String> castMetrics = new ArrayList<String>();
    private String castUsername;
    private String castPassword;
    
    public List<String> getCastServers() {
        return castServers;
    }
    public List<Double> getCastVersions() {
        return castVersions;
    }

    public List<String> getCastMetrics() {
        return castMetrics;
    }

    public String getCastUsername() {
        return castUsername;
    }

    public String getCastPassword() {
        return castPassword;
    }
    
    public static CastCollector prototype(List<String> servers, List<Double> versions, List<String> metrics) {
    	CastCollector protoType = new CastCollector();
        protoType.setName("cast");
        protoType.setCollectorType(CollectorType.CodeQuality);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        if(servers!=null) {
            protoType.getCastServers().addAll(servers);
        }
        if(versions!=null) {
            protoType.getCastVersions().addAll(versions);
        }
        if(metrics!=null) {
            protoType.getCastMetrics().addAll(metrics);
        }

        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(CastProject.APPLICATION_ID, "");
        allOptions.put(CastProject.APPLICATION_NAME, "");
        allOptions.put(CastProject.SNAPSHOT_ID, "");
        protoType.setAllFields(allOptions);

        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put(CastProject.APPLICATION_ID,"");
        uniqueOptions.put(CastProject.APPLICATION_NAME,"");
        uniqueOptions.put(CastProject.SNAPSHOT_ID,"");
        protoType.setUniqueFields(uniqueOptions);
        return protoType;
    }

}
