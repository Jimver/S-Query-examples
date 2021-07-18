package org.example.dh.state;

import org.example.dh.events.OrderState;

import java.util.Date;

public class OrderStatusState {
    private String orderState;
    private Date updateTimestamp;

    public OrderStatusState(String orderState, Date updateTimestamp) {
        this.orderState = orderState;
        this.updateTimestamp = updateTimestamp;
    }

    public OrderStatusState() {
        updateTimestamp = new Date(); // Prevent nullpointer exception on serialization
    }

    public String getOrderState() {
        return orderState;
    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    /**
     * Set order status, only valid transitions are:
     * ORDER_RECEIVED -> SENT_TO_VENDOR, SENT_TO_VENDOR -> VENDOR_ACCEPTED, VENDOR_ACCEPTED -> NOTIFIED, NOTIFIED -> ACCEPTED
     * ACCEPTED -> NEAR_VENDOR, NEAR_VENDOR -> PICKED_UP, PICKED_UP -> LEFT_PICKUP, LEFT_PICKUP -> NEAR_CUSTOMER
     * NEAR_CUSTOMER -> DELIVERED, DELIVERED -> ORDER_COMPLETED.
     * @param orderState Order state to transition to
     * @return True if successfully transitioned, false otherwise
     */
    private boolean trySetOrderState(String orderState) {
        if (orderState == null) {
            return false;
        }
        if (this.orderState == null && orderState.equals(OrderState.ORDER_RECEIVED)) {
            this.orderState = OrderState.ORDER_RECEIVED;
            return true;
        }
        if (this.orderState == null) {
            return false;
        }
        if (this.orderState.equals(OrderState.ORDER_RECEIVED) && orderState.equals(OrderState.SENT_TO_VENDOR)) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState.equals(OrderState.SENT_TO_VENDOR) && orderState.equals(OrderState.VENDOR_ACCEPTED)) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState.equals(OrderState.VENDOR_ACCEPTED) && orderState.equals(OrderState.NOTIFIED)) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState.equals(OrderState.NOTIFIED) && orderState.equals(OrderState.ACCEPTED)) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState.equals(OrderState.ACCEPTED) && orderState.equals(OrderState.NEAR_VENDOR)) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState.equals(OrderState.NEAR_VENDOR) && orderState.equals(OrderState.PICKED_UP)) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState.equals(OrderState.PICKED_UP) && orderState.equals(OrderState.LEFT_PICKUP)) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState.equals(OrderState.LEFT_PICKUP) && orderState.equals(OrderState.NEAR_CUSTOMER)) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState.equals(OrderState.NEAR_CUSTOMER) && orderState.equals(OrderState.DELIVERED)) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState.equals(OrderState.DELIVERED) && orderState.equals(OrderState.ORDER_COMPLETED)) {
            this.orderState = orderState;
            return true;
        }
        return false;
    }

    public void updateOrderState(String orderState, long updateTimestamp) {
        if (trySetOrderState(orderState)) {
            this.updateTimestamp = new Date(updateTimestamp);
        }
    }
}
