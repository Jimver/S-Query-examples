package org.example.userdemo;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import userdemo.SqlHelper;

public class MainUserJoinQueryJob {
    public static void main(String[] args) {
        JetInstance jet = Jet.bootstrappedInstance();
        SqlHelper.queryJoinGivenMapNames("tracking-map", "order-map",
                "user-tracking", "user-order", jet);
    }
}
