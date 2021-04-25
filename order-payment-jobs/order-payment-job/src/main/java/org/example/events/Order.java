package org.example.events;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

public class Order extends Event {
    private final long orderId;
    private final long itemId;
    private final boolean operation;

    public Order(long id, long timestamp, long orderId, long itemId, boolean operation) {
        super(id, timestamp);
        this.orderId = orderId;
        this.itemId = itemId;
        this.operation = operation;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getItemId() {
        return itemId;
    }

    public boolean getOperation() {
        return operation;
    }

    public static class OrderSerializer implements StreamSerializer<Order> {

        @Override
        public int getTypeId() {
            return 1;
        }

        @Override
        public void write(ObjectDataOutput out, Order order) throws IOException {
            out.writeLong(order.id());
            out.writeLong(order.timestamp());
            out.writeLong(order.getOrderId());
            out.writeLong(order.getItemId());
            out.writeBoolean(order.getOperation());
        }

        @Override
        public Order read(ObjectDataInput in) throws IOException {
            long id = in.readLong();
            long timestamp = in.readLong();
            long orderId = in.readLong();
            long itemId = in.readLong();
            boolean operation = in.readBoolean();
            return new Order(id, timestamp, orderId, itemId, operation);
        }
    }
}
