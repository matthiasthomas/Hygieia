package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CastProject;
import com.capitalone.dashboard.model.CodeQuality;
import java.util.List;


public interface CastClient {

    List<CastProject> getApplications(String instanceUrl);
    CodeQuality currentCodeQuality(CastProject project);
}
