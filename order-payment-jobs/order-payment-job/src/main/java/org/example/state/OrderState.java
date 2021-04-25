package org.example.state;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

public class OrderState {
    private long size;
    private long total;

    public OrderState() {
        this.size = 0;
        this.total = 0;
    }

    public OrderState(long size, long total) {
        this.size = size;
        this.total = total;
    }

    public long getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }

    public void incrementSize() {
        size++;
    }

    public void decrementSize() {
        size--;
    }

    public void deltaTotal(long delta) {
        total += delta;
    }

    public static class OrderStateSerializer implements StreamSerializer<OrderState> {
        @Override
        public int getTypeId() {
            return 3;
        }

        @Override
        public void write(ObjectDataOutput out, OrderState orderState) throws IOException {
            out.writeLong(orderState.size);
            out.writeLong(orderState.total);
        }

        @Override
        public OrderState read(ObjectDataInput in) throws IOException {
            long size = in.readLong();
            long total = in.readLong();
            return new OrderState(size, total);
        }

    }
}
