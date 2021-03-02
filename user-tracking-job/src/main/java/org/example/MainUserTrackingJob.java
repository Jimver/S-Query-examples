package org.example;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.config.ProcessingGuarantee;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamStage;

import static java.util.concurrent.TimeUnit.SECONDS;

public class MainUserTrackingJob {
    public static void main(String[] args) {
        JobConfig config = new JobConfig();
        config.setName("user-tracking");
        config.setProcessingGuarantee(ProcessingGuarantee.EXACTLY_ONCE);
        config.setSnapshotIntervalMillis(SECONDS.toMillis(10)); // Snapshot every 10s
        Pipeline p = Pipeline.create();
        StreamStage<UserEvent> src = p
                .readFrom(UserEvent.itemStream(2, 10)) // Stream of random UserEvents (2 per second)
                .withNativeTimestamps(SECONDS.toMillis(5)); // Use native timestamps)
        src
                .groupingKey(UserEvent::getUserId)
                .mapStateful(
                        // The time the state can live before being evicted
                        SECONDS.toMillis(10),
                        // Method creating a new state object (for each unique groupingKey)
                        TrackingState::new,
                        // Method that maps a given event and corresponding key to the new output given the state
                        (state, key, userEvent) -> {
                            state.incrementViews(userEvent.getCategory());
//                            return String.format("ID: %d, %s", key, state.getViews(userEvent.getCategory()));
                            return String.format("Most popular for %d: %s", key, state.mostViews());
                        },
                        // Method that executes when states belonging to a key are evicted by watermarks
                        (state, key, currentWatermark) -> "Evicted key: " + key
                ).setName("tracking-map")
                .writeTo(Sinks.logger());

        JetInstance jet = Jet.bootstrappedInstance();
        jet.newJob(p, config).join();
    }
}
