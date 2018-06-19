package com.capitalone.dashboard.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Extension of Collector that stores current build server configuration.
 */
public class CASTCollector extends Collector {
    public static CASTCollector prototype() {
    	CASTCollector protoType = new CASTCollector();
          protoType.setName("CAST");
          protoType.setCollectorType(CollectorType.CAST);
          protoType.setOnline(true);
          protoType.setEnabled(true);
        
          Map<String, Object> options = new HashMap<>();
          protoType.setAllFields(options);
          protoType.setUniqueFields(options);
          return protoType;
    }
}
