package org.example;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class OrderPaymentQueryBenchmark {
    private static final AtomicBoolean lock = new AtomicBoolean(false);

    private static boolean getLock() {
        return lock.compareAndSet(false, true);
    }

    private static void releaseLock() {
        lock.set(false);
    }

    private static void printLatencies(List<Long> ssidLatencies, List<Long> queryLatencies) {
        while(!getLock()) {
            System.out.println();
            System.out.println("SSID latencies");
            System.out.println(ssidLatencies);
            System.out.println("Query latencies");
            System.out.println(queryLatencies);
            releaseLock();
        }
    }

    public static void main(String[] args) {
        JetInstance jet = Jet.bootstrappedInstance();

        int queryInterval = 1000;
        if (args.length >= 1) {
            queryInterval = Integer.parseInt(args[0]);
        }
        int printEvery = 10;
        if (args.length >= 2) {
            printEvery = Integer.parseInt(args[1]);
        }

        long[] res;
        List<Long> ssidLatencies = new ArrayList<>();
        List<Long> queryLatencies = new ArrayList<>();

        final AtomicBoolean stop = new AtomicBoolean(false);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            printLatencies(ssidLatencies, queryLatencies);
            stop.set(true);
        }));

        int counter = 0;

        while(!stop.get()) {
            res = SqlHelper.queryJoinGivenMapNames("order", "payment",
                    "OrderPaymentBenchmark", "OrderPaymentBenchmark", jet, false);
            long ssidLatency = res[0];
            while(!getLock()) {
                ssidLatencies.add(ssidLatency);
                releaseLock();
            }
            long queryLatency = res[1];
            if (queryLatency == -1) {
                System.err.println("Query failed!");
            } else {
                while(!getLock()) {
                    queryLatencies.add(queryLatency);
                    releaseLock();
                }
            }
            counter++;
            if ((printEvery != -1) && counter % printEvery == 0) {
                printLatencies(ssidLatencies, queryLatencies);
            }
            try {
                Thread.sleep(queryInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
                printLatencies(ssidLatencies, queryLatencies);
                break;
            }
        }

    }
}
