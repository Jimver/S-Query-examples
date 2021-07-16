package org.example.dh.events;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

public class OrderStateSerializer implements StreamSerializer<OrderState> {

    @Override
    public void write(ObjectDataOutput out, OrderState object) throws IOException {
        switch (object) {
            case ORDER_RECEIVED -> out.writeInt(0);
            case SENT_TO_VENDOR -> out.writeInt(1);
            case VENDOR_ACCEPTED -> out.writeInt(2);
            case NOTIFIED -> out.writeInt(3);
            case ACCEPTED -> out.writeInt(4);
            case NEAR_VENDOR -> out.writeInt(5);
            case PICKED_UP -> out.writeInt(6);
            case LEFT_PICKUP -> out.writeInt(7);
            case NEAR_CUSTOMER -> out.writeInt(8);
            case DELIVERED -> out.writeInt(9);
            case ORDER_COMPLETED -> out.writeInt(10);
        }
    }

    @Override
    public OrderState read(ObjectDataInput in) throws IOException {
        return fromInt(in.readInt());
    }

    public static OrderState fromInt(int i) {
        switch (i) {
            case 0 -> {
                return OrderState.ORDER_RECEIVED;
            }
            case 1 -> {
                return OrderState.SENT_TO_VENDOR;
            }
            case 2 -> {
                return OrderState.VENDOR_ACCEPTED;
            }
            case 3 -> {
                return OrderState.NOTIFIED;
            }
            case 4 -> {
                return OrderState.ACCEPTED;
            }
            case 5 -> {
                return OrderState.NEAR_VENDOR;
            }
            case 6 -> {
                return OrderState.PICKED_UP;
            }
            case 7 -> {
                return OrderState.LEFT_PICKUP;
            }
            case 8 -> {
                return OrderState.NEAR_CUSTOMER;
            }
            case 9 -> {
                return OrderState.DELIVERED;
            }
            case 10 -> {
                return OrderState.ORDER_COMPLETED;
            }
            default -> throw new IllegalArgumentException("Invalid order state");
        }
    }

    @Override
    public int getTypeId() {
        return 4;
    }
}
