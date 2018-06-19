package com.capitalone.dashboard.model;

/**
 * Enumerates the possible {@link Collector} types.
 */
public enum CollectorType {
    SCM,
    CMDB,
    Build,
    Artifact,
    Deployment,
    AgileTool,
    CAST,
    ProductCAST,
    @Deprecated
    Feature,
    @Deprecated
    ScopeOwner,
    @Deprecated
    Scope,
    CodeQuality,
    Test,
    StaticSecurityScan,
    LibraryPolicy,
    ChatOps,
    Cloud,
    Product,
    ProductBuild,
    ProductCodeQuality,
    ProductSCM,
    Aggregate,
	//customcollector
	Monitor,
    AppPerformance,
    InfraPerformance,
    ServiceNow,
    ProductServiceNow,
    FunctionalResults,
    Sonar,
    ProductSonar,
    ProductFunctionalResults,
    IntegrationResults,
    ProductIntegrationResults,
    UnitTestResults,
    ProductUnitTestResults;
    
    public static CollectorType fromString(String value) {
        for (CollectorType collectorType : values()) {
            if (collectorType.toString().equalsIgnoreCase(value)) {
                return collectorType;
            }
        }
        throw new IllegalArgumentException(value + " is not a CollectorType");
    }
}
