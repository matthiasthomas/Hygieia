package com.capitalone.dashboard.collector;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CastAIPClientSelector {

    //private final DefaultSonar6Client sonar6Client;
    private final DefaultCastAIPClient castClient;

    @Autowired
    public CastAIPClientSelector(DefaultCastAIPClient castClient) {
        //this.sonar6Client = sonar6Client;
        this.castClient = castClient;
    }

    public CastAIPClient getCastClient(Double version) {
        return ((version == null) || (version < 6.3)) ? castClient:castClient;
    }
}
