package org.example;

import com.hazelcast.jet.datamodel.TimestampedItem;
import com.hazelcast.query.extractor.ValueCollector;
import com.hazelcast.query.extractor.ValueExtractor;

public class LongArrayExtractor implements ValueExtractor<TimestampedItem<Long[]>, String> {
    @Override
    public void extract(TimestampedItem<Long[]> timestampedItem, String integer, ValueCollector valueCollector) {
        valueCollector.addObject(timestampedItem.item()[Integer.parseInt(integer)]);
    }
}
