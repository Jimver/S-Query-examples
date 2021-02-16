package org.example;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.map.IMap;

public class SampleImapsJob {
    public static void main(String[] args) {
        JetInstance jet = Jet.bootstrappedInstance();
        HazelcastInstance hz = jet.getHazelcastInstance();
        IMap<Long, Long> sampleone = hz.getMap("sampleone");
        IMap<Long, Long> sampletwo = hz.getMap("sampletwo");

        sampleone.put(0L, 0L);
        sampleone.put(1L, 1L);
        sampleone.put(2L, 2L);

        sampletwo.put(0L, 1L);
        sampletwo.put(1L, 2L);
        sampletwo.put(2L, 3L);
    }
}
