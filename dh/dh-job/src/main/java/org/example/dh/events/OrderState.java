package org.example.dh.events;

public enum OrderState {
    ORDER_RECEIVED,
    SENT_TO_VENDOR,
    VENDOR_ACCEPTED,
    NOTIFIED,
    ACCEPTED,
    NEAR_VENDOR,
    PICKED_UP,
    LEFT_PICKUP,
    NEAR_CUSTOMER,
    DELIVERED,
    ORDER_COMPLETED
}
