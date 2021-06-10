package org.example;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.jet.impl.processor.IMapStateHelper;
import com.hazelcast.jet.impl.processor.SnapshotIMapKey;
import com.hazelcast.map.IMap;

import java.util.Collection;

public class DirectQueryJob {
    /**
     * Main method.
     * @param args Array of arguments:
     *             1. name of vertex to query snapshot state of
     *             2. Job name
     *             3. Amount of keys to query (nothing or 0 means all)
     */
    public static void main(String[] args) {
        if (args.length <= 1 || args.length > 3) {
            throw new IllegalArgumentException("Amount of arguments must be 2 or 3");
        }
        String vertex = args[0];
        String job = args[1];
        int amountOfKeys = 0;
        if (args.length >= 3) {
            amountOfKeys = Integer.parseInt(args[2]);
            if (amountOfKeys < 0) {
                throw new IllegalArgumentException("Amount of keys must be 0 or higher!");
            }
        }
        int finalAmountOfKeys = amountOfKeys;

        HazelcastInstance hz = HazelcastClient.newHazelcastClient();

        String snapshotMapName = IMapStateHelper.getPhaseSnapshotMapName(vertex);
        String snapshotIdName = IMapStateHelper.getSnapshotIdName(job);

        IMap<SnapshotIMapKey<Long>, Object> imap = hz.getMap(snapshotMapName);
        IAtomicLong snapshotId = hz.getCPSubsystem().getAtomicLong(snapshotIdName);

        long latestSnapshotId = snapshotId.get();

        System.out.println("Latest snapshot id: " + latestSnapshotId);

        Collection<Object> result;

        long beforeGetAll = System.nanoTime();
        if (finalAmountOfKeys == 0) {
            result = imap.values(new SnapshotPredicate(latestSnapshotId));
        } else {
            result = imap.values(new SnapshotRangePredicate(latestSnapshotId, finalAmountOfKeys));
        }
        long afterGetAll = System.nanoTime();
        System.out.println("Get took: " + (afterGetAll - beforeGetAll));

        System.out.println(result.size());
    }
}
