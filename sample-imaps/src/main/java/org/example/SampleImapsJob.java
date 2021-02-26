package org.example;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.sql.SqlResult;
import com.hazelcast.sql.SqlRow;

import java.io.Serializable;

class Employee implements Serializable {
    private String name;
    private long age;

    public Employee(String name, long age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public long getAge() {
        return age;
    }
}

public class SampleImapsJob {
    public static void main(String[] args) {
        JetInstance jet = Jet.bootstrappedInstance();
        HazelcastInstance hz = jet.getHazelcastInstance();
        IMap<Long, Employee> sampleone = hz.getMap("sampleone");
        IMap<Long, Employee> sampletwo = hz.getMap("sampletwo");

        sampleone.put(0L, new Employee("name1", 30));
        sampleone.put(1L, new Employee("name2", 40));
        sampleone.put(2L, new Employee("name3", 50));

        sampletwo.put(0L, new Employee("name1", 31));
        sampletwo.put(1L, new Employee("name2", 41));
        sampletwo.put(2L, new Employee("name3", 51));

        try (SqlResult result = hz.getSql().execute(
                "SELECT sampleone.name, sampletwo.name, sampleone.age, sampletwo.age FROM sampleone JOIN sampletwo ON sampleone.name=sampletwo.name")) {
            for (SqlRow row : result) {
                String name1 = row.getObject(0);
                String name2 = row.getObject(1);
                long age1 = row.getObject(2);
                long age2 = row.getObject(3);

                System.out.printf("%s, %s, %d, %d%n", name1, name2, age1, age2);
            }
        }
    }
}
