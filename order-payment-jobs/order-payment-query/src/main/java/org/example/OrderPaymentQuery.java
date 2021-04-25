package org.example;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;

public class OrderPaymentQuery {
    public static void main(String[] args) {
        JetInstance jet = Jet.bootstrappedInstance();
        SqlHelper.queryJoinGivenMapNames("order", "payment",
                "OrderPaymentBenchmark", "OrderPaymentBenchmark", jet);
    }
}
