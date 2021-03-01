package org.example;

import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.StreamSource;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class UserEvent implements Serializable {
    private final long userId;
    private final Category category;

    public UserEvent(long userId, Category category) {
        this.userId = userId;
        this.category = category;
    }

    public static StreamSource<UserEvent> itemStream(
            int itemsPerSecond,
            long numIds
    ) {
        return SourceBuilder.timestampedStream("loginEventStream", ctx -> new LoginEventStreamSource(itemsPerSecond, numIds))
                .fillBufferFn(LoginEventStreamSource::fillBuffer)
                .build();
    }

    public long getUserId() {
        return userId;
    }

    public Category getCategory() {
        return category;
    }

    private static final class LoginEventStreamSource {
        private static final int MAX_BATCH_SIZE = 1024;

        private final long periodNanos;

        private long emitSchedule;
        private final long numIds;
        private final int maxCategories = Category.values().length;
        private final Category[] categories = Category.values();

        private LoginEventStreamSource(int itemsPerSecond, long numIds) {
            this.numIds = numIds;
            this.periodNanos = TimeUnit.SECONDS.toNanos(1) / itemsPerSecond;
        }

        void fillBuffer(SourceBuilder.TimestampedSourceBuffer<UserEvent> buf) {
            long nowNs = System.nanoTime();
            if (emitSchedule == 0) {
                emitSchedule = nowNs;
            }
            // round ts down to nearest period
            long tsNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis());
            long ts = TimeUnit.NANOSECONDS.toMillis(tsNanos - (tsNanos % periodNanos));
            for (int i = 0; i < MAX_BATCH_SIZE && nowNs >= emitSchedule; i++) {
                long userId = ThreadLocalRandom.current().nextLong(numIds); // Random user id
                int categoryIndex = ThreadLocalRandom.current().nextInt(maxCategories); // Random category (index)
                UserEvent item = new UserEvent(userId, categories[categoryIndex]);
                buf.add(item, ts);
                emitSchedule += periodNanos;
            }
        }
    }
}
