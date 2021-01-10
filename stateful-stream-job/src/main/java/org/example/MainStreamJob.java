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
                        SECONDS.toMillis(3),
                        () -> new Long[]{0L},
                        (state, id, event) -> {
                            state[0]++;
                            state[0]++;
                            return state[0];
                        },
                        (state, id, currentWatermark) -> id
                ).setLocalParallelism(1)
                .writeTo(Sinks.logger()).setLocalParallelism(1);
        jet = Jet.bootstrappedInstance();
        hz = jet.getHazelcastInstance();

        jet.newJob(p, config).join();
    }
}
