package com.capitalone.dashboard.model;

public enum AggregateWidgetType {
    JenkinsBuild,
    CodeQuality,
    ServiceNow,
    ProjectVelocity,
    Repo;

    public static AggregateWidgetType fromString(String value) {
        for (AggregateWidgetType type : values()) {
            if (type.toString().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid AggregateWidgetType.");
    }
}
