package org.example.dh.state;

import java.util.Date;

public class RiderLocationState {
    private Date updateTimestamp;
    private double longitude;
    private double latitude;

    public RiderLocationState(Date updateTimestamp, double longitude, double latitude) {
        this.updateTimestamp = updateTimestamp;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public RiderLocationState() {

    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
