package org.example.dh.state;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;
import java.util.Date;

public class RiderLocationStateSerializer implements StreamSerializer<RiderLocationState> {
    @Override
    public int getTypeId() {
        return 6;
    }

    @Override
    public void write(ObjectDataOutput out, RiderLocationState riderLocationState) throws IOException {
        out.writeLong(riderLocationState.getUpdateTimestamp().getTime());
        out.writeDouble(riderLocationState.getLongitude());
        out.writeDouble(riderLocationState.getLatitude());
    }

    @Override
    public RiderLocationState read(ObjectDataInput in) throws IOException {
        Date updateTimestamp = new Date(in.readLong());
        double longitude = in.readDouble();
        double latitude = in.readDouble();
        return new RiderLocationState(updateTimestamp, longitude, latitude);
    }
}