package org.example.dh.events;

import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

public class OrderInfo extends Event {
    public final long orderId;
    public final double longitudeVendor, latitudeVendor, longitudeCustomer, latitudeCustomer, longitudeDeliveryZone, latitudeDeliveryZone;
    public final String deliveryZone;
    public final long promisedDeliveryTimestamp, committedPickupAtTimestamp;
    public final int preparationTime;

    public OrderInfo(long id, long timestamp, long orderId, double longitudeVendor, double latitudeVendor, double longitudeCustomer, double latitudeCustomer, double longitudeDeliveryZone, double latitudeDeliveryZone, String deliveryZone, long promisedDeliveryTimestamp, long committedPickupAtTimestamp, int preparationTime) {
        super(id, timestamp);
        this.orderId = orderId;
        this.longitudeVendor = longitudeVendor;
        this.latitudeVendor = latitudeVendor;
        this.longitudeCustomer = longitudeCustomer;
        this.latitudeCustomer = latitudeCustomer;
        this.longitudeDeliveryZone = longitudeDeliveryZone;
        this.latitudeDeliveryZone = latitudeDeliveryZone;
        this.promisedDeliveryTimestamp = promisedDeliveryTimestamp;
        this.committedPickupAtTimestamp = committedPickupAtTimestamp;
        this.deliveryZone = deliveryZone;
        this.preparationTime = preparationTime;
    }

    public static class OrderInfoSerializer implements StreamSerializer<OrderInfo> {

        @Override
        public void write(ObjectDataOutput out, OrderInfo object) throws IOException {
            Event.write(out, object);
            out.writeLong(object.orderId);
            out.writeDouble(object.longitudeVendor);
            out.writeDouble(object.latitudeVendor);
            out.writeDouble(object.longitudeCustomer);
            out.writeDouble(object.latitudeCustomer);
            out.writeDouble(object.longitudeDeliveryZone);
            out.writeDouble(object.latitudeDeliveryZone);
            out.writeUTF(object.deliveryZone);
            out.writeLong(object.promisedDeliveryTimestamp);
            out.writeLong(object.committedPickupAtTimestamp);
            out.writeInt(object.preparationTime);
        }

        @Override
        public OrderInfo read(ObjectDataInput in) throws IOException {
            Tuple2<Long, Long> event = Event.readEvent(in);
            long orderId = in.readLong();
            double longitudeVendor = in.readDouble();
            double latitudeVendor = in.readDouble();
            double longitudeCustomer = in.readDouble();
            double latitudeCustomer = in.readDouble();
            double longitudeDeliveryZone = in.readDouble();
            double latitudeDeliveryZone = in.readDouble();
            String deliveryZone = in.readUTF();
            long promisedDeliveryTimestamp = in.readLong();
            long committedPickupAtTimestamp = in.readLong();
            int preparationTime = in.readInt();
            return new OrderInfo(event.f0(), event.f1(), orderId, longitudeVendor, latitudeVendor, longitudeCustomer, latitudeCustomer, longitudeDeliveryZone, latitudeDeliveryZone, deliveryZone, promisedDeliveryTimestamp, committedPickupAtTimestamp, preparationTime);
        }

        @Override
        public int getTypeId() {
            return 1;
        }
    }
}
