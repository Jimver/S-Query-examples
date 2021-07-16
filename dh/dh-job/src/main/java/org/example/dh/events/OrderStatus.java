package org.example.dh.events;

import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

public class OrderStatus extends Event {
    private final long orderId;
    private final OrderState orderState;

    public OrderStatus(long id, long timestamp, long orderId, OrderState orderState) {
        super(id, timestamp);
        this.orderId = orderId;
        this.orderState = orderState;
    }

    public long getOrderId() {
        return orderId;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public static class OrderStatusSerializer implements StreamSerializer<OrderStatus> {

        @Override
        public void write(ObjectDataOutput out, OrderStatus object) throws IOException {
            Event.write(out, object);
            out.writeLong(object.orderId);
            out.writeObject(object.orderState);
        }

        @Override
        public OrderStatus read(ObjectDataInput in) throws IOException {
            Tuple2<Long, Long> event = Event.readEvent(in);
            long orderId = in.readLong();
            OrderState orderState = in.readObject();
            return new OrderStatus(event.f0(), event.f1(), orderId, orderState);
        }

        @Override
        public int getTypeId() {
            return 3;
        }
    }
}
