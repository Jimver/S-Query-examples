package org.example.dh.state;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;
import java.util.Date;

public class OrderStatusStateSerializer implements StreamSerializer<OrderStatusState> {
    @Override
    public void write(ObjectDataOutput out, OrderStatusState object) throws IOException {
        out.writeUTF(object.getOrderState());
        out.writeLong(object.getUpdateTimestamp().getTime());
    }

    @Override
    public OrderStatusState read(ObjectDataInput in) throws IOException {
        return new OrderStatusState(in.readUTF(), new Date(in.readLong()));
    }

    @Override
    public int getTypeId() {
        return 7;
    }
}
