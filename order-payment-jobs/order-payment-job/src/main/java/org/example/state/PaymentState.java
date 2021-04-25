package org.example.state;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import org.example.events.Payment;

import java.io.IOException;

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

    public static class PaymentStateSerializer implements StreamSerializer<PaymentState> {
        @Override
        public int getTypeId() {
            return 4;
        }

        @Override
        public void write(ObjectDataOutput out, PaymentState paymentState) throws IOException {
            out.writeShort(paymentState.getPaymentStatus());
        }

        @Override
        public PaymentState read(ObjectDataInput in) throws IOException {
            short paymentStatus = in.readShort();
            return new PaymentState(paymentStatus);
        }

    }
}
