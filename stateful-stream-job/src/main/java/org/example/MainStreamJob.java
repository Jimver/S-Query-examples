package org.example;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.test.TestSources;

import static java.util.concurrent.TimeUnit.SECONDS;

public class MainStreamJob {
    private static JetInstance jet;
    private static HazelcastInstance hz;
    public static void main(String[] args) {
        JobConfig config = new JobConfig();
        config.setName("jet-test");
        Pipeline p = Pipeline.create();
        p.readFrom(TestSources.itemStream(5))
                .withNativeTimestamps(10L).setLocalParallelism(1)
                .groupingKey(e -> e.sequence())
                .mapStateful(
                        // The time the state can live before being evicted
                        SECONDS.toMillis(3),
                        // Method creating a new state object
                        () -> new Long[]{0L},
                        // Method that maps a given event and corresponding key to the new output given the state
                        (state, key, event) -> {
                            state[0]++;
                            return state[0];
                        },
                        // Method that executes when states belonging to a key are evicted by watermarks
                        (state, key, currentWatermark) -> key
                ).setLocalParallelism(1)
                .writeTo(Sinks.logger()).setLocalParallelism(1);
        jet = Jet.bootstrappedInstance();
        jet.newJob(p, config).join();
    }
}
