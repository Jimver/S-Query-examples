package org.example;

import com.hazelcast.sql.SqlColumnMetadata;
import com.hazelcast.sql.SqlResult;
import com.hazelcast.sql.SqlRow;
import com.hazelcast.sql.SqlRowMetadata;

import java.util.ArrayList;
import java.util.List;

public class SqlHelper {
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
            } else {
                row.append("%n");
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
