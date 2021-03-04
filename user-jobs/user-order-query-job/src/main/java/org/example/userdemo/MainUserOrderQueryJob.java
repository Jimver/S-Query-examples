package org.example.userdemo;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.impl.processor.TransformStatefulP;
import com.hazelcast.sql.SqlResult;
import userdemo.SqlHelper;

import java.util.List;
import java.util.Map;

public class MainUserOrderQueryJob {
    public static void main(String[] args) {
        JetInstance jet = Jet.bootstrappedInstance();
        HazelcastInstance hz = jet.getHazelcastInstance();

        List<String> stateMapNames = hz.getList(TransformStatefulP.STATE_IMAP_NAMES_LIST_NAME);
        Map<String, String> snapshotMapNames = hz.getMap(TransformStatefulP.VERTEX_TO_SS_IMAP_NAME);

        boolean querySs = true;
        String liveMap = stateMapNames.get(0);
        String ssMap = snapshotMapNames.get("order-map");

        IAtomicLong distributedSnapshotId = hz.getCPSubsystem().getAtomicLong("ssid-" + stateMapNames.get(0));

        System.out.println(ssMap);
        long snapshotId = distributedSnapshotId.get();
        long querySnapshotId = Math.max(0, snapshotId - 1);
        System.out.printf("Latest snapshot id: %d, querying: %d%n", snapshotId, querySnapshotId);
        String queryMap = querySs ? ssMap : liveMap;
        String queryString = String.format("SELECT * FROM \"%s\" WHERE snapshotId=%d", queryMap, querySnapshotId);
        System.out.println(queryString);

        try (SqlResult result = hz.getSql().execute(queryString)) {
            SqlHelper.resultToHeaderAndRows(result).forEach(System.out::println);
        }
    }
}
