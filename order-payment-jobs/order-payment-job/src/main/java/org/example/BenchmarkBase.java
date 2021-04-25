package org.example;

import com.hazelcast.function.FunctionEx;
import com.hazelcast.internal.util.HashUtil;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.accumulator.LongLongAccumulator;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.config.ProcessingGuarantee;
import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamStage;
import org.HdrHistogram.Histogram;
import org.example.events.Order;
import org.example.events.Payment;
import org.example.state.OrderState;
import org.example.state.OrderStateSerializer;
import org.example.state.PaymentState;
import org.example.state.PaymentStateSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Properties;

import static com.hazelcast.jet.datamodel.Tuple2.tuple2;
import static java.lang.Integer.parseInt;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.example.EventSourceP.simpleTime;

public abstract class BenchmarkBase {
    public static final String PROPS_FILENAME = "order-payments.properties";
    public static final String PROPS_PAYMENTS_PER_SECOND = "payments-per-second";
    public static final String PROP_NUM_DISTINCT_ITEM_IDS = "num-distinct-item-ids";
    public static final String PROP_NUM_DISTINCT_ORDER_IDS = "num-distinct-order-ids";
    public static final String PROP_PROCESSING_GUARANTEE = "processing-guarantee";
    public static final String PROP_SNAPSHOT_INTERVAL_MILLIS = "snapshot-interval-millis";
    public static final String PROP_WARMUP_SECONDS = "warmup-seconds";
    public static final String PROP_MEASUREMENT_SECONDS = "measurement-seconds";
    public static final String PROP_LATENCY_REPORTING_THRESHOLD_MILLIS = "latency-reporting-threshold-millis";
    public static final String PROP_OUTPUT_PATH = "output-path";

    static final long WARMUP_REPORTING_INTERVAL_MS = SECONDS.toMillis(2);
    static final long MEASUREMENT_REPORTING_INTERVAL_MS = SECONDS.toMillis(10);
    static final long BENCHMARKING_DONE_REPORT_INTERVAL_MS = SECONDS.toMillis(1);
    static final String BENCHMARK_DONE_MESSAGE = "----------------------benchmarking is done----------------------";
    static final long INITIAL_SOURCE_DELAY_MILLIS = 10;

    private int latencyReportingThresholdMs;

    BenchmarkBase() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Supply one argument: the simple class name of a benchmark");
            return;
        }
        String pkgName = BenchmarkBase.class.getPackage().getName();
        BenchmarkBase benchmark = (BenchmarkBase)
                Class.forName(pkgName + '.' + args[0]).getDeclaredConstructor().newInstance();
        benchmark.run();
    }

    void run() {
        String benchmarkName = getClass().getSimpleName();
        Properties props = loadProps();
        var jobCfg = new JobConfig();
        jobCfg.setName(benchmarkName);
        jobCfg.registerSerializer(Order.class, Order.OrderSerializer.class);
        jobCfg.registerSerializer(Payment.class, Payment.PaymentSerializer.class);
        jobCfg.registerSerializer(OrderState.class, OrderStateSerializer.class);
        jobCfg.registerSerializer(PaymentState.class, PaymentStateSerializer.class);
        var jet = Jet.bootstrappedInstance();
        try {
            int paymentsPerSecond = parseIntProp(props, PROPS_PAYMENTS_PER_SECOND);
            int numDistinctItemIds = parseIntProp(props, PROP_NUM_DISTINCT_ITEM_IDS);
            int numDistinctOrderIds = parseIntProp(props, PROP_NUM_DISTINCT_ORDER_IDS);
            String pgString = ensureProp(props, PROP_PROCESSING_GUARANTEE);
            ProcessingGuarantee guarantee = ProcessingGuarantee.valueOf(pgString.toUpperCase().replace('-', '_'));
            int snapshotInterval = parseIntProp(props, PROP_SNAPSHOT_INTERVAL_MILLIS);
            int warmupSeconds = parseIntProp(props, PROP_WARMUP_SECONDS);
            int measurementSeconds = parseIntProp(props, PROP_MEASUREMENT_SECONDS);
            latencyReportingThresholdMs = parseIntProp(props, PROP_LATENCY_REPORTING_THRESHOLD_MILLIS);
            String outputPath = ensureProp(props, PROP_OUTPUT_PATH);
            System.out.printf(
                    "Benchmark name               %s%n" +
                    "Payments per second          %,d%n" +
                    "Distinct item ids            %,d%n" +
                    "Distinct order ids           %,d%n" +
                    "Processing guarantee         %s%n" +
                    "Snapshot interval            %,d ms%n" +
                    "Warmup period                %,d s%n" +
                    "Measurement period           %,d s%n" +
                    "Latency reporting threshold  %,d ms%n" +
                    "Output path                  %s%n",
                    benchmarkName,
                    paymentsPerSecond,
                    numDistinctItemIds,
                    numDistinctOrderIds,
                    guarantee,
                    snapshotInterval,
                    warmupSeconds,
                    measurementSeconds,
                    latencyReportingThresholdMs,
                    outputPath
            );
            long warmupTimeMillis = SECONDS.toMillis(warmupSeconds);
            long totalTimeMillis = SECONDS.toMillis(warmupSeconds + measurementSeconds);

            var pipeline = Pipeline.create();
            var latencies = addComputation(pipeline, props);
            latencies.filter(t2 -> t2.f0() < totalTimeMillis)
                     .map(t2 -> String.format("%d,%d", t2.f0(), t2.f1()))
                     .writeTo(Sinks.files(new File(outputPath, "log").getPath()));
            latencies
                    .mapStateful(
                            () -> new RecordLatencyHistogram(warmupTimeMillis, totalTimeMillis),
                            RecordLatencyHistogram::map)
                    .writeTo(Sinks.files(new File(outputPath, "histogram").getPath()));

            jobCfg.setProcessingGuarantee(guarantee);
            jobCfg.setSnapshotIntervalMillis(snapshotInterval);
            var job = jet.newJob(pipeline, jobCfg);
            Runtime.getRuntime().addShutdownHook(new Thread(job::cancel));
            job.join();
        } catch (ValidationException e) {
            System.err.println(e.getMessage());
        }
    }

    abstract StreamStage<Tuple2<Long, Long>> addComputation(
            Pipeline pipeline, Properties props
    ) throws ValidationException;

    static Properties loadProps() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(PROPS_FILENAME));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Can't read file " + PROPS_FILENAME);
            System.exit(2);
        }
        return props;
    }

    public static String ensureProp(Properties props, String propName) throws ValidationException {
        String prop = props.getProperty(propName);
        if (prop == null || prop.isEmpty()) {
            throw new ValidationException("Missing property: " + propName);
        }
        return prop;
    }

    public static int parseIntProp(Properties props, String propName) throws ValidationException {
        String prop = ensureProp(props, propName);
        try {
            return parseInt(prop.replace("_", ""));
        } catch (NumberFormatException e) {
            throw new ValidationException(
                    "Invalid property format, correct example is 9_999: " + propName + "=" + prop);
        }
    }

    /**
     * Random number
     * @param seq sequence number
     * @param range Amount of possible values to generate
     * @return Random value in [0, range-1]
     */
    public static long getRandom(long seq, long range) {
        return Math.abs(HashUtil.fastLongMix(seq)) % range;
    }

    <T> FunctionEx<StreamStage<T>, StreamStage<Tuple2<Long, Long>>> determineLatency(FunctionEx<? super T, ? extends Long> timestampFn) {
        int latencyReportingThresholdLocal = this.latencyReportingThresholdMs;
        return stage ->
                stage.map(timestampFn)
                     .mapStateful(LongLongAccumulator::new, /* (startTimestamp, lastTimestamp)*/
                             (state, timestamp) -> {
                                 long lastTimestamp = state.get2();
                                 if (timestamp <= lastTimestamp) {
                                     return null;
                                 }
                                 if (lastTimestamp == 0) {
                                     state.set1(timestamp); // state.startTimestamp = timestamp;
                                 }
                                 long startTimestamp = state.get1();
                                 state.set2(timestamp); // state.lastTimestamp = timestamp;

                                 long latency = System.currentTimeMillis() - timestamp;
                                 if (latency == -1) { // very low latencies may be reported as negative due to clock skew
                                     latency = 0;
                                 }
                                 if (latency < 0) {
                                     throw new RuntimeException("Negative latency: " + latency);
                                 }
                                 long time = simpleTime(timestamp);
                                 if (latency >= (long) latencyReportingThresholdLocal) {
                                     System.out.format("time %,d: latency %,d ms%n", time, latency);
                                 }
                                 return tuple2(timestamp - startTimestamp, latency);
                             });
    }

    private static class RecordLatencyHistogram implements Serializable {
        private static final long serialVersionUID = 351252345L;
        private final long warmupTimeMillis;
        private final long totalTimeMillis;
        private long benchmarkDoneLastReport;
        private long warmingUpLastReport;
        private long measurementLastReport;
        private Histogram histogram = new Histogram(5);

        public RecordLatencyHistogram(long warmupTimeMillis, long totalTimeMillis) {
            this.warmupTimeMillis = warmupTimeMillis;
            this.totalTimeMillis = totalTimeMillis;
        }

        @SuppressWarnings("ConstantConditions")
        String map(Tuple2<Long, Long> timestampAndLatency) {
            long timestamp = timestampAndLatency.f0();
            String timeMsg = String.format("%,d ", totalTimeMillis - timestamp);
            if (histogram == null) {
                long benchmarkDoneNow = timestamp / BENCHMARKING_DONE_REPORT_INTERVAL_MS;
                if (benchmarkDoneNow > benchmarkDoneLastReport) {
                    benchmarkDoneLastReport = benchmarkDoneNow;
                    System.out.format(BENCHMARK_DONE_MESSAGE + " -- %s%n", timeMsg);
                }
                return null;
            }
            if (timestamp < warmupTimeMillis) {
                long warmingUpNow = timestamp / WARMUP_REPORTING_INTERVAL_MS;
                if (warmingUpNow > warmingUpLastReport) {
                    warmingUpLastReport = warmingUpNow;
                    System.out.format("warming up -- %s%n", timeMsg);
                }
            } else {
                long measurementNow = timestamp / MEASUREMENT_REPORTING_INTERVAL_MS;
                if (measurementNow > measurementLastReport) {
                    measurementLastReport = measurementNow;
                    System.out.println(timeMsg);
                }
                histogram.recordValue(timestampAndLatency.f1());
            }
            if (timestamp >= totalTimeMillis) {
                System.out.println("Printing histogram");
                try {
                    return exportHistogram(histogram);
                } finally {
                    histogram = null;
                }
            }
            return null;
        }

        private static String exportHistogram(Histogram histogram) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(bos);
            histogram.outputPercentileDistribution(out, 1.0);
            out.close();
            String res = bos.toString();
            System.out.println(res);
            return res;
        }
    }
}
