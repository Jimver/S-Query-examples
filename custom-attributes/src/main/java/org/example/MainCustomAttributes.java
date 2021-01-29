package org.example;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.impl.processor.TransformStatefulP;
import com.hazelcast.map.IMap;

public class MainCustomAttributes {
    public static void main(String[] args) {
        JobConfig config = new JobConfig();
        config.setName("custom-attributes");

        JetInstance jet = Jet.bootstrappedInstance();
        HazelcastInstance hz = jet.getHazelcastInstance();

        IMap<String, String> stateMapNames = hz.getMap(TransformStatefulP.CUSTOM_ATTRIBUTE_IMAP_NAME);
        stateMapNames.put("longarr", LongArrayExtractor.class.getName());
//        stateMapNames.put(CustomState.class.getSimpleName(), GenericLongExtractor.class.getName());
    }
}
