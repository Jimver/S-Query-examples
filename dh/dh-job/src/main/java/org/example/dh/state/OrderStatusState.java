package org.example.dh.state;

import org.example.dh.events.OrderState;

public class OrderStatusState {
    private OrderState orderState;

    public OrderStatusState(OrderState orderState) {
        this.orderState = orderState;
    }

    public OrderStatusState() {
    }

    public OrderState getOrderState() {
        return orderState;
    }

    /**
     * Set order status, only valid transitions are:
     * ORDER_RECEIVED -> SENT_TO_VENDOR, SENT_TO_VENDOR -> VENDOR_ACCEPTED, VENDOR_ACCEPTED -> NOTIFIED, NOTIFIED -> ACCEPTED
     * ACCEPTED -> NEAR_VENDOR, NEAR_VENDOR -> PICKED_UP, PICKED_UP -> LEFT_PICKUP, LEFT_PICKUP -> NEAR_CUSTOMER
     * NEAR_CUSTOMER -> DELIVERED, DELIVERED -> ORDER_COMPLETED.
     * @param orderState Order state to transition to
     * @return True if successfully transitioned, false otherwise
     */
    public boolean setOrderState(OrderState orderState) {
        if (this.orderState == null && orderState == OrderState.ORDER_RECEIVED) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState == OrderState.ORDER_RECEIVED && orderState == OrderState.SENT_TO_VENDOR) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState == OrderState.SENT_TO_VENDOR && orderState == OrderState.NOTIFIED) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState == OrderState.NOTIFIED && orderState == OrderState.ACCEPTED) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState == OrderState.ACCEPTED && orderState == OrderState.NEAR_VENDOR) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState == OrderState.NEAR_VENDOR && orderState == OrderState.PICKED_UP) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState == OrderState.PICKED_UP && orderState == OrderState.LEFT_PICKUP) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState == OrderState.LEFT_PICKUP && orderState == OrderState.NEAR_CUSTOMER) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState == OrderState.NEAR_CUSTOMER && orderState == OrderState.DELIVERED) {
            this.orderState = orderState;
            return true;
        }
        if (this.orderState == OrderState.DELIVERED && orderState == OrderState.ORDER_COMPLETED) {
            this.orderState = orderState;
            return true;
        }
        return false;
    }
}
