package com.capitalone.dashboard.collector;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CastClientSelector {

    //private final DefaultSonar6Client sonar6Client;
    private final DefaultCastClient castClient;

    @Autowired
    public CastClientSelector(DefaultCastClient castClient) {
        //this.sonar6Client = sonar6Client;
        this.castClient = castClient;
    }

    public CastClient getCastClient(Double version) {
        return ((version == null) || (version < 6.3)) ? castClient:castClient;
    }
}