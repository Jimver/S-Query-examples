package org.example.dh.state;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;
import java.util.Date;

public class OrderInfoStateSerializer implements StreamSerializer<OrderInfoState> {

    @Override
    public void write(ObjectDataOutput out, OrderInfoState object) throws IOException {
        out.writeDouble(object.getLongitudeVendor());
        out.writeDouble(object.getLatitudeVendor());
        out.writeDouble(object.getLongitudeCustomer());
        out.writeDouble(object.getLatitudeCustomer());
        out.writeDouble(object.getLongitudeDeliveryZone());
        out.writeDouble(object.getLatitudeDeliveryZone());
        out.writeUTF(object.getDeliveryZone());
        out.writeLong(object.getPromisedDeliveryTimestamp().getTime());
        out.writeLong(object.getCommittedPickupAtTimestamp().getTime());
        out.writeInt(object.getPreparationTime());
    }

    @Override
    public OrderInfoState read(ObjectDataInput in) throws IOException {
        double longitudeVendor = in.readDouble();
        double latitudeVendor = in.readDouble();
        double longitudeCustomer = in.readDouble();
        double latitudeCustomer = in.readDouble();
        double longitudeDeliveryZone = in.readDouble();
        double latitudeDeliveryZone = in.readDouble();
        String deliveryZone = in.readUTF();
        Date promisedDeliveryTimestamp = new Date(in.readLong());
        Date committedPickupAtTimestamp = new Date(in.readLong());
        int preparationTime = in.readInt();
        return new OrderInfoState(longitudeVendor, latitudeVendor, longitudeCustomer, latitudeCustomer, longitudeDeliveryZone, latitudeDeliveryZone, deliveryZone, promisedDeliveryTimestamp, committedPickupAtTimestamp, preparationTime);
    }

    @Override
    public int getTypeId() {
        return 5;
    }
}