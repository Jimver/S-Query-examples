package org.example;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.datamodel.TimestampedItem;
import com.hazelcast.jet.impl.processor.TransformStatefulP;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.Predicates;
import com.hazelcast.sql.SqlResult;
import com.hazelcast.sql.SqlRow;

import java.util.Collection;
import java.util.List;

public class MainQuery {
    public static void main(String[] args) {
        JetInstance jet = Jet.bootstrappedInstance();
        HazelcastInstance hz = jet.getHazelcastInstance();

        List<String> stateMapNames = hz.getList(TransformStatefulP.STATE_IMAP_NAMES_LIST_NAME);
        String[] imapsArray = stateMapNames.toArray(new String[0]);
        System.out.println("State IMaps:");
        for (String s : imapsArray) {
            System.out.println("\t" + s);
        }

        for (String stateIMap : imapsArray) {
            System.out.println("Results for state IMap: " + stateIMap);
            System.out.println("SQL Query results:");
            try (SqlResult result = hz.getSql().execute(String.format("SELECT * FROM \"%s\"", stateIMap))) {
                for (SqlRow row : result) {
                    Object name = row.getMetadata();

                    System.out.println(name);

                    Long ts = row.getObject("timestamp");
                    Long[] stateArr = row.getObject("item");
                    System.out.println(String.format("\tts: %d, state: %d", ts, stateArr[0]));
                }
                System.out.println("Done");
            } catch (Exception e) {

            }

            IMap state = hz.getMap(imapsArray[0]);

            PredicateBuilder.EntryObject e = Predicates.newPredicateBuilder().getEntryObject();
            Predicate predicate = e.get("longarr[0]").greaterEqual(1);
            Collection<TimestampedItem<Long[]>> greaterOne = state.values(predicate);
            System.out.println("Predicate result ");
            greaterOne.forEach(ts -> System.out.println(String.format("\tts: %d, state: %d", ts.getTimestamp(), ts.getItem()[0])));

            String queryGreaterOne = "longarr[0] > 1";
            Collection<TimestampedItem<Long[]>> greaterOneSQL = state.values(Predicates.sql(queryGreaterOne));
            System.out.println(String.format("Predicate result using SQL string: '%s'", queryGreaterOne));
            greaterOneSQL.forEach(ts -> System.out.println(String.format("\tts: %d, state: %d", ts.getTimestamp(), ts.getItem()[0])));
        }
    }
}
