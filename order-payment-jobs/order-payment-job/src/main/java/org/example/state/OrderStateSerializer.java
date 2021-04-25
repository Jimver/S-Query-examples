package org.example.state;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

public class OrderStateSerializer implements StreamSerializer<OrderState> {
    @Override
    public int getTypeId() {
        return 3;
    }

    @Override
    public void write(ObjectDataOutput out, OrderState orderState) throws IOException {
        out.writeLong(orderState.getSize());
        out.writeLong(orderState.getTotal());
    }

    @Override
    public OrderState read(ObjectDataInput in) throws IOException {
        long size = in.readLong();
        long total = in.readLong();
        return new OrderState(size, total);
    }
}