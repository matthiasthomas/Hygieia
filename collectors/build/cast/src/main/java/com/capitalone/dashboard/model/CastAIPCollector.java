/**
 * 
 */
package com.capitalone.dashboard.model;

/**
 * @author KKH
 *
 */
import com.capitalone.dashboard.model.CastAIPProject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class CastAIPCollector extends Collector{

	/**
	 * 
	 */
    private List<String> castServers = new ArrayList<String>();
    private List<Double> castVersions = new ArrayList<Double>();
    private List<String> castMetrics = new ArrayList<String>();
    
    public List<String> getCastAIPServers() {
        return castServers;
    }
    public List<Double> getCastAIPVersions() {
        return castVersions;
    }

    public List<String> getCastAIPMetrics() {
        return castMetrics;
    }
    
    public static CastAIPCollector prototype(List<String> servers, List<Double> versions, List<String> metrics) {
    	CastAIPCollector protoType = new CastAIPCollector();
        protoType.setName("cast");
        protoType.setCollectorType(CollectorType.CodeQuality);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        if(servers!=null) {
            protoType.getCastAIPServers().addAll(servers);
        }
        if(versions!=null) {
            protoType.getCastAIPVersions().addAll(versions);
        }
        if(metrics!=null) {
            protoType.getCastAIPMetrics().addAll(metrics);
        }

        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(CastAIPProject.INSTANCE_URL,"");
        allOptions.put(CastAIPProject.APPLICATION_ID, "");
        allOptions.put(CastAIPProject.APPLICATION_NAME, "");
        allOptions.put(CastAIPProject.SNAPSHOT_ID, "");
        protoType.setAllFields(allOptions);

        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put(CastAIPProject.INSTANCE_URL,"");
        uniqueOptions.put(CastAIPProject.APPLICATION_ID,"");
        uniqueOptions.put(CastAIPProject.APPLICATION_NAME,"");
        uniqueOptions.put(CastAIPProject.SNAPSHOT_ID,"");
        protoType.setUniqueFields(uniqueOptions);
        return protoType;
    }

}
