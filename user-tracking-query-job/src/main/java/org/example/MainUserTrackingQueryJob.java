package org.example;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.impl.processor.TransformStatefulP;
import com.hazelcast.sql.SqlColumnMetadata;
import com.hazelcast.sql.SqlColumnType;
import com.hazelcast.sql.SqlResult;
import com.hazelcast.sql.SqlRow;
import com.hazelcast.sql.SqlRowMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainUserTrackingQueryJob {
    public static void main(String[] args) {
        JetInstance jet = Jet.bootstrappedInstance();
        HazelcastInstance hz = jet.getHazelcastInstance();

        List<String> stateMapNames = hz.getList(TransformStatefulP.STATE_IMAP_NAMES_LIST_NAME);
        Map<String, String> snapshotMapNames = hz.getMap(TransformStatefulP.VERTEX_TO_SS_IMAP_NAME);

        boolean query_ss = true;
        String liveMap = stateMapNames.get(0);
        String ssMap = snapshotMapNames.get("tracking-map");

        IAtomicLong distributedSnapshotId = hz.getCPSubsystem().getAtomicLong("ssid-" + stateMapNames.get(0));

        System.out.println(ssMap);
        long snapshotId = distributedSnapshotId.get();
        String queryMap = query_ss ? ssMap : liveMap;
        String queryString = String.format("SELECT * FROM \"%s\" WHERE snapshotId=%d", queryMap, snapshotId);
        System.out.println(queryString);

        try (SqlResult result = hz.getSql().execute(queryString)) {
            SqlRowMetadata rowMetadata = result.getRowMetadata();
            StringBuilder header = new StringBuilder();
            StringBuilder row = new StringBuilder();
            List<Integer> indicesToString = new ArrayList<>();
            for (int i = 0; i < rowMetadata.getColumnCount(); i++) {
                SqlColumnMetadata columnMetadata = rowMetadata.getColumn(i);
                header.append(columnMetadata.getName());
                header.append(" (");
                header.append(columnMetadata.getType().getValueClass().getName());
                header.append(")");
                if (i < rowMetadata.getColumnCount() - 1) {
                    header.append(",\t");
                } else {
                    header.append("%n");
                }
                switch (columnMetadata.getType()) {
                    case VARCHAR:
                        row.append("%s");
                        break;
                    case TINYINT:
                    case BIGINT:
                    case SMALLINT:
                    case INTEGER:
                        row.append("%d");
                        break;
                    case DOUBLE:
                    case REAL:
                    case DECIMAL:
                        row.append("%.2f");
                        break;
                    case BOOLEAN:
                        row.append("%b");
                        break;
                    case DATE:
                    case NULL:
                    case TIME:
                    case OBJECT:
                    case TIMESTAMP:
                    case TIMESTAMP_WITH_TIME_ZONE:
                        row.append("%s");
                        indicesToString.add(i);
                        break;
                }
                if (i < rowMetadata.getColumnCount() - 1) {
                    row.append(",\t");
                } else {
                    row.append("%n");
                }
            }
            System.out.println(header.toString());
            String rowString = row.toString();
            for (SqlRow resultRow : result) {
                List<Object> printObjects = new ArrayList<>(rowMetadata.getColumnCount());
                for (int i = 0; i < rowMetadata.getColumnCount(); i++) {
                    if (indicesToString.contains(i)) {
                        printObjects.add(resultRow.getObject(i).toString());
                    } else {
                        printObjects.add(resultRow.getObject(i));
                    }
                }
                System.out.printf(rowString, printObjects.toArray());
            }
        }
    }
}
