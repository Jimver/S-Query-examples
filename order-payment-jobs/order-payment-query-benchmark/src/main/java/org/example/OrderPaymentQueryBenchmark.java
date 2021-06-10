package org.example;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderPaymentQueryBenchmark {
    private static void printLatencies(List<Long> ssidLatencies, List<Long> queryLatencies, long beforeAll) {
            System.out.println();
            System.out.println("SSID latencies");
            System.out.println(ssidLatencies);
            System.out.println("Query latencies");
            System.out.println(queryLatencies);
            System.out.println("Total time");
            long timeDelta = System.nanoTime() - beforeAll;
            System.out.println(timeDelta);
            System.out.println("Total latencies");
            int size = queryLatencies.size();
            System.out.println(size);
            System.out.println("Q/s");
            System.out.println((double)size/ (timeDelta/1000000000.0));
    }

    /**
     * Main method
     * Run from hazelcast/config as working directory:
     * java -cp "../lib/*" org.example.OrderPaymentQueryBenchmark 0 -1 2 10
     * @param args Array of arguments:
     *             1. Query interval in ms (0 means no pause)
     *             2. print latencies every x queries (-1 means don't print on interval)
     *             3. Limit of the amount of keys to query, (-1 means query everything)
     *             4. Concurrent amount of threads to query with, (must be 1 or higher)
     */
    public static void main(String[] args) {
        // Use below code when submitting as Jet job
//        JetInstance jet = Jet.bootstrappedInstance();
//        HazelcastInstance hz = jet.getHazelcastInstance();
        // use below code when running directly
        HazelcastInstance hz = HazelcastClient.newHazelcastClient();

        int queryInterval = 1000;
        if (args.length >= 1) {
            queryInterval = Integer.parseInt(args[0]);
            if (queryInterval < 0) {
                throw new IllegalArgumentException("Query interval should be 0 or higher!");
            }
        }
        int printEvery = 10;
        if (args.length >= 2) {
            printEvery = Integer.parseInt(args[1]);
            if (printEvery != -1 && printEvery <= 0) {
                throw new IllegalArgumentException("Print every should be -1 or 1 or higher!");
            }
        }
        int limit = -1;
        String[] queryArgs = new String[]{"", "", ""};
        if (args.length >= 3) {
            limit = Integer.parseInt(args[2]);
            if (limit < 0) {
                throw new IllegalArgumentException("Query limit should be 0 or higher!");
            }
            queryArgs[1] = String.format("CAST(t1.partitionKey AS int) < %d AND CAST(t2.partitionKey AS int) < %d", limit, limit);
        }
        int concurrentThreads = 1;
        if (args.length >= 4) {
            concurrentThreads = Integer.parseInt(args[3]);
            if (concurrentThreads <= 0) {
                throw new IllegalArgumentException("Concurrent threads should be 1 or higher!");
            }
        }
        // We need final versions of the settings to pass to the threads.
        final int finalLimit = limit;
        final int finalPrintEvery = printEvery;
        final int finalQueryInterval = queryInterval;

        // Thread safe variables
        List<Long> ssidLatencies = Collections.synchronizedList(new ArrayList<>());
        List<Long> queryLatencies = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger counter = new AtomicInteger(0);
        final AtomicBoolean stop = new AtomicBoolean(false);

        long beforeAll = System.nanoTime();
        ExecutorService threadPool = Executors.newFixedThreadPool(concurrentThreads);
        for (int i = 0; i < concurrentThreads; i++) {
            threadPool.submit(() -> {
                long[] res;
                while(!stop.get()) {
                    if (finalLimit == -1) {
                        res = SqlHelper.queryJoinGivenMapNames("order", "payment",
                                "OrderPaymentBenchmark", "OrderPaymentBenchmark", hz, false);
                    } else {
                        res = SqlHelper.queryJoinGivenMapNames("order", "payment",
                                "OrderPaymentBenchmark", "OrderPaymentBenchmark", hz, false, queryArgs);
                    }
                    long ssidLatency = res[0];
                    ssidLatencies.add(ssidLatency);
                    long queryLatency = res[1];
                    if (queryLatency == -1) {
                        System.err.println("Query failed!");
                    } else {
                        queryLatencies.add(queryLatency);
                    }
                    int counterResult = counter.incrementAndGet();
                    if ((finalPrintEvery != -1) && counterResult % finalPrintEvery == 0) {
                        printLatencies(ssidLatencies, queryLatencies, beforeAll);
                    }
                    if (finalQueryInterval > 0) {
                        try {
                            Thread.sleep(finalQueryInterval);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                            printLatencies(ssidLatencies, queryLatencies, beforeAll);
                            break;
                        }
                    }
                }
            });
        }
        // No more jobs to submit so shutdown the pool
        threadPool.shutdown();
        // On cancellation, stop the threads and print the latencies
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop.set(true);
            try {
                boolean success = threadPool.awaitTermination(10, TimeUnit.SECONDS);
                printLatencies(ssidLatencies, queryLatencies, beforeAll);
                if (!success) {
                    System.err.println("Timeout while awaiting thread termination");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.err.println("Interrupted while awaiting thread termination");
            }
        }));
        // Sleep loop to prevent job from terminating
        while (!stop.get()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
