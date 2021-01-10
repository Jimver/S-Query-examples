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

        System.out.println("Query task is running");

        List<String> stateMapNames = hz.getList(TransformStatefulP.STATE_IMAP_NAMES_LIST_NAME);
        String[] imapsArray = stateMapNames.toArray(new String[0]);
        System.out.println("List map names:");
        for (String s : imapsArray) {
            System.out.println(s);
        }

        IMap state = hz.getMap(imapsArray[0]);
        Collection<Object> entries = state.entrySet();


        for (Object entry : entries) {
            System.out.println(entry);
        }

        while(true) {
            System.out.println("SQL Query results");
            try (SqlResult result = hz.getSql().execute(String.format("SELECT * FROM \"%s\"", imapsArray[0]))) {
                for (SqlRow row : result) {
                    Object name = row.getMetadata();

                    System.out.println(name);

                    Long ts = row.getObject("timestamp");
                    Long[] stateArr = row.getObject("item");
                    System.out.println(String.format("ts: %d, state: %d", ts, stateArr[0]));
                }
                System.out.println("Done");
            } catch (Exception e) {

            }

            PredicateBuilder.EntryObject e = Predicates.newPredicateBuilder().getEntryObject();
            Predicate predicate = e.get("longarr[0]").greaterEqual(1);
            Collection<TimestampedItem<Long[]>> greaterOne = state.values(predicate);
            System.out.println("Predicate result ");
            greaterOne.forEach(ts -> System.out.println(String.format("ts: %d, state: %d", ts.getTimestamp(), ts.getItem()[0])));

            String queryGreaterOne = "longarr[0] > 1";
            Collection<TimestampedItem<Long[]>> greaterOneSQL = state.values(Predicates.sql(queryGreaterOne));
            System.out.println(String.format("Predicate result using SQL string: '%s'", queryGreaterOne));
            greaterOneSQL.forEach(ts -> System.out.println(String.format("ts: %d, state: %d", ts.getTimestamp(), ts.getItem()[0])));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            break;
//            System.out.println("Query again");
        }
    }
}
