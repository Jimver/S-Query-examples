package userdemo;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.impl.processor.TransformStatefulP;
import com.hazelcast.sql.SqlColumnMetadata;
import com.hazelcast.sql.SqlResult;
import com.hazelcast.sql.SqlRow;
import com.hazelcast.sql.SqlRowMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlHelper {
    public static void queryGivenMapName(String transformName, JetInstance jet) {
        HazelcastInstance hz = jet.getHazelcastInstance();

        Map<String, String> stateMapNames = hz.getMap(TransformStatefulP.VERTEX_TO_LIVE_STATE_IMAP_NAME);
        Map<String, String> snapshotMapNames = hz.getMap(TransformStatefulP.VERTEX_TO_SS_STATE_IMAP_NAME);
        Map<String, String> snapshotIdNames = hz.getMap(TransformStatefulP.VERTEX_TO_SS_ID_IMAP_NAME);

        boolean querySs = true;
        String liveMapName = stateMapNames.get(transformName);
        String ssMapName = snapshotMapNames.get(transformName);
        String ssIdName = snapshotIdNames.get(transformName);

        IAtomicLong distributedSnapshotId = hz.getCPSubsystem().getAtomicLong(ssIdName);

        System.out.println(ssMapName);
        long snapshotId = distributedSnapshotId.get();
        long querySnapshotId = Math.max(0, snapshotId - 1);
        System.out.printf("Latest snapshot id: %d, querying: %d%n", snapshotId, querySnapshotId);
        String queryMap = querySs ? ssMapName : liveMapName;
        String queryString = String.format("SELECT * FROM \"%s\" WHERE snapshotId=%d", queryMap, querySnapshotId);
        System.out.println(queryString);

        try (SqlResult result = hz.getSql().execute(queryString)) {
            resultToHeaderAndRows(result).forEach(System.out::println);
        }
    }

    public static List<String> resultToHeaderAndRows(SqlResult result) {
        List<String> arrayResult = new ArrayList<>();
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
            }
        }
        arrayResult.add(header.toString());
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
            arrayResult.add(String.format(rowString, printObjects.toArray()));
        }
        return arrayResult;
    }
}
