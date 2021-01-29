package org.example;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.test.TestSources;

import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class MainStreamJob {
    private static JetInstance jet;
    private static HazelcastInstance hz;
    public static void main(String[] args) {
        JobConfig config = new JobConfig();
        config.setName("jet-test");
        config.setInitialSnapshotName("statefulss");
        config.setSnapshotIntervalMillis(SECONDS.toMillis(10)); // Snapshot every 10s
        Logger.getGlobal().info("Initial snapshot name: " + config.getInitialSnapshotName());
        System.out.println("Initial snapshot name: " + config.getInitialSnapshotName());
        Pipeline p = Pipeline.create();
        p.readFrom(TestSources.itemStream(2)) // Stream of increasing numbers: 0,1,2,3,...
                .withNativeTimestamps(SECONDS.toMillis(5)) // Use native timestamps
                .groupingKey(e -> e.sequence()) // Group the events by their sequence number
                .mapStateful(
                        // The time the state can live before being evicted
                        SECONDS.toMillis(30),
                        // Method creating a new state object (for each unique groupingKey)
                        () -> new Long[]{0L},
                        // Method that maps a given event and corresponding key to the new output given the state
                        (state, key, event) -> {
                            state[0]++;
                            return state[0];
                        },
                        // Method that executes when states belonging to a key are evicted by watermarks
                        (state, key, currentWatermark) -> "Evicted key: " + key
                )
                .writeTo(Sinks.logger());
        jet = Jet.bootstrappedInstance();
        jet.newJob(p, config).join();
    }
}
