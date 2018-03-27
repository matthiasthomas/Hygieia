/**
 * 
 */
package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CollectorItem;

/**
 * @author KKH
 *
 */
public class CastAIPCollectorItem extends CollectorItem{
    protected static final String INSTANCE_URL = "instanceUrl";
    protected static final String APPLICATION_NAME = "appName";
    protected static final String APPLICATION_ID = "appId";
    protected static final String SNAPSHOT_ID = "snapId";
    
    public String getInstanceUrl() {
        return (String) getOptions().get(INSTANCE_URL);
    }

    public void setInstanceUrl(String instanceUrl) {
        getOptions().put(INSTANCE_URL, instanceUrl);
    }

    public String getApplicationId() {
        return (String) getOptions().get(APPLICATION_ID );
    }

    public void setApplicationId(String id) {
        getOptions().put(APPLICATION_ID , id);
    }

    public String getApplicationName() {
        return (String) getOptions().get(APPLICATION_NAME );
    }

    public void setApplicationName(String name) {
        getOptions().put(APPLICATION_NAME, name);
    }
    
    public String getSnapshotId() {
        return (String) getOptions().get(SNAPSHOT_ID);
    }

    public void setSnapshotId(String name) {
        getOptions().put(SNAPSHOT_ID, name);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CastAIPCollectorItem that = (CastAIPCollectorItem) o;
        return getApplicationId().equals(that.getApplicationId()) && getInstanceUrl().equals(that.getInstanceUrl()) && getSnapshotId().equals(that.getSnapshotId());
    }

    public int hashCode() {
        int result = getInstanceUrl().hashCode();
        result = 31 * result + getApplicationId().hashCode();
        return result;
    }
}
