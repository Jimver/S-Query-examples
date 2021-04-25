package org.example.state;

import static org.example.events.Payment.PaymentStatus.ORDERED;
import static org.example.events.Payment.PaymentStatus.PAID;
import static org.example.events.Payment.PaymentStatus.REFUNDED;

public class PaymentState {
    private short paymentStatus;

    public PaymentState() {
        this.paymentStatus = ORDERED;
    }

    public PaymentState(short paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public short getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * Set payment status, only valid transitions are ORDERED -> PAID, PAID -> REFUNDED.
     * @param paymentStatus Payment status to set
     * @return True if successfully set, false otherwise
     */
    public boolean setPaymentStatus(short paymentStatus) {
        if (this.paymentStatus == ORDERED && paymentStatus == PAID) {
                this.paymentStatus = PAID;
                return true;
        } else if (this.paymentStatus == PAID && paymentStatus == REFUNDED) {
            this.paymentStatus = REFUNDED;
            return true;
        }
        return false;
    }
}
