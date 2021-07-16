package org.example.dh.state;


import java.util.Date;

public class OrderInfoState {
    private double longitudeVendor;
    private double latitudeVendor;
    private double longitudeCustomer;
    private double latitudeCustomer;
    private double longitudeDeliveryZone;
    private double latitudeDeliveryZone;
    private String deliveryZone;
    private Date promisedDeliveryTimestamp, committedPickupAtTimestamp;
    private int preparationTime;

    public OrderInfoState(double longitudeVendor, double latitudeVendor, double longitudeCustomer, double latitudeCustomer, double longitudeDeliveryZone, double latitudeDeliveryZone, String deliveryZone, Date promisedDeliveryTimestamp, Date committedPickupAtTimestamp, int preparationTime) {
        this.longitudeVendor = longitudeVendor;
        this.latitudeVendor = latitudeVendor;
        this.longitudeCustomer = longitudeCustomer;
        this.latitudeCustomer = latitudeCustomer;
        this.longitudeDeliveryZone = longitudeDeliveryZone;
        this.latitudeDeliveryZone = latitudeDeliveryZone;
        this.promisedDeliveryTimestamp = promisedDeliveryTimestamp;
        this.committedPickupAtTimestamp = committedPickupAtTimestamp;
        this.deliveryZone = deliveryZone;
        this.preparationTime = preparationTime;
    }

    public OrderInfoState() {

    }

    public double getLongitudeVendor() {
        return longitudeVendor;
    }

    public double getLatitudeVendor() {
        return latitudeVendor;
    }

    public double getLongitudeCustomer() {
        return longitudeCustomer;
    }

    public double getLatitudeCustomer() {
        return latitudeCustomer;
    }

    public double getLongitudeDeliveryZone() {
        return longitudeDeliveryZone;
    }

    public double getLatitudeDeliveryZone() {
        return latitudeDeliveryZone;
    }

    public String getDeliveryZone() {
        return deliveryZone;
    }

    public Date getPromisedDeliveryTimestamp() {
        return promisedDeliveryTimestamp;
    }

    public Date getCommittedPickupAtTimestamp() {
        return committedPickupAtTimestamp;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setLongitudeVendor(double longitudeVendor) {
        this.longitudeVendor = longitudeVendor;
    }

    public void setLatitudeVendor(double latitudeVendor) {
        this.latitudeVendor = latitudeVendor;
    }

    public void setLongitudeCustomer(double longitudeCustomer) {
        this.longitudeCustomer = longitudeCustomer;
    }

    public void setLatitudeCustomer(double latitudeCustomer) {
        this.latitudeCustomer = latitudeCustomer;
    }

    public void setLongitudeDeliveryZone(double longitudeDeliveryZone) {
        this.longitudeDeliveryZone = longitudeDeliveryZone;
    }

    public void setLatitudeDeliveryZone(double latitudeDeliveryZone) {
        this.latitudeDeliveryZone = latitudeDeliveryZone;
    }

    public void setDeliveryZone(String deliveryZone) {
        this.deliveryZone = deliveryZone;
    }

    public void setPromisedDeliveryTimestamp(Date promisedDeliveryTimestamp) {
        this.promisedDeliveryTimestamp = promisedDeliveryTimestamp;
    }

    public void setCommittedPickupAtTimestamp(Date committedPickupAtTimestamp) {
        this.committedPickupAtTimestamp = committedPickupAtTimestamp;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }
}
