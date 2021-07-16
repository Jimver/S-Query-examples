package org.example.dh.state;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

public class OrderStatusStateSerializer implements StreamSerializer<OrderStatusState> {
    @Override
    public void write(ObjectDataOutput out, OrderStatusState object) throws IOException {
        out.writeObject(object.getOrderState());
    }

    @Override
    public OrderStatusState read(ObjectDataInput in) throws IOException {
        return new OrderStatusState(in.readObject());
    }

    @Override
    public int getTypeId() {
        return 7;
    }
}
