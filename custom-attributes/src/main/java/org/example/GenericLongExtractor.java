package org.example;

import com.hazelcast.jet.datamodel.TimestampedItem;
import com.hazelcast.query.extractor.ValueCollector;
import com.hazelcast.query.extractor.ValueExtractor;

public class GenericLongExtractor<T> implements ValueExtractor<TimestampedItem<T>, String> {
    @Override
    public void extract(TimestampedItem<T> target, String argument, ValueCollector collector) {
        collector.addObject(GetterUtil.callGetter(target.item(), argument));
    }
}
