/**
 * 
 */
package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CollectorItem;

/**
 *
 */
public class CastCollectorItem extends CollectorItem {
    private Integer applicationId;
    private Integer snapshotId;

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(Integer snapshotId) {
        this.snapshotId = snapshotId;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CastCollectorItem that = (CastCollectorItem) o;
        return getApplicationId().equals(that.getApplicationId()) && getSnapshotId().equals(that.getSnapshotId());
    }

    public int hashCode() {
        int result = getApplicationId().hashCode();
        return result;
    }
}
