package org.example;

import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.datamodel.Tuple3;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.StreamStage;
import org.example.events.Order;
import org.example.events.Payment;
import org.example.state.OrderState;
import org.example.state.PaymentState;

import java.util.Properties;

import static com.hazelcast.jet.datamodel.Tuple2.tuple2;
import static com.hazelcast.jet.datamodel.Tuple3.tuple3;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.example.EventSourceP.orderSource;
import static org.example.EventSourceP.paymentSource;

public class OrderPaymentBenchmark extends BenchmarkBase {
    @Override
    StreamStage<Tuple2<Long, Long>> addComputation(Pipeline pipeline, Properties props) throws ValidationException {
        int numDistinctOrderIds = parseIntProp(props, PROP_NUM_DISTINCT_ORDER_IDS);
        int paymentsPerSecond = parseIntProp(props, PROPS_PAYMENTS_PER_SECOND);
        int numItemIds = parseIntProp(props, PROP_NUM_DISTINCT_ITEM_IDS);
        int ordersPerSecond = paymentsPerSecond * 3;

        // Generate payments at rate eventPerSecond, each payment refers to orderId = seq / 3
        // Generate order at rate eventsPerSecond, each order refers to orderId = seq / 9
        StreamStage<Order> orders = pipeline
                .readFrom(orderSource(ordersPerSecond, INITIAL_SOURCE_DELAY_MILLIS, numDistinctOrderIds, numItemIds))
                .withNativeTimestamps(0);
        StreamStage<Payment> payments = pipeline
                .readFrom(paymentSource(paymentsPerSecond, INITIAL_SOURCE_DELAY_MILLIS, numDistinctOrderIds))
                .withNativeTimestamps(0);

        // Order processor, outputs: (order size, total price, and timestamp)
        StreamStage<Tuple3<Long, Long, Long>> ordersProcessor = orders.groupingKey(Order::getOrderId)
                .mapStateful(
//                        SECONDS.toMillis(5), // 5 second TTL
                        OrderState::new,
                        (state, key, order) -> {
                            if (order.getOperation()) {
                                state.incrementSize();
                                state.deltaTotal(order.getItemId()); // Price is just the item id
                            } else {
                                state.decrementSize();
                                state.deltaTotal(-order.getItemId()); // Negative price
                            }
                            return tuple3(state.getSize(), state.getTotal(), order.timestamp());
                        }
//                        ,(state, key, currentWatermark) -> null // Do nothing on evict
                )
                .setName("order");

        // Payment processor, outputs: (payment status, timestamp)
        StreamStage<Tuple2<Short, Long>> paymentProcessor = payments.groupingKey(Payment::getOrderId)
                .mapStateful(
                        SECONDS.toMillis(5), // 5 second TTL
                        PaymentState::new,
                        (state, key, payment) -> {
                            boolean success = state.setPaymentStatus(payment.getPaymentStatus());
//                            if (success) {
//                                System.out.println("set payment status success");
//                            } else {
//                                System.out.println("set payment status failure");
//                            }
                            return tuple2(state.getPaymentStatus(), payment.timestamp());
                        }
                        ,(state, key, watermark) -> null // Do nothing on evict
                )
                .setName("payment");

        // Measure latencies for order and payment events
        var latencies = ordersProcessor.apply(determineLatency(Tuple3::f2));
        var paymentLatencies = paymentProcessor.apply(determineLatency(Tuple2::f1));

        // Combine latencies
        return latencies.merge(paymentLatencies);
    }
}
