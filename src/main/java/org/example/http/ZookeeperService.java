package org.example.http;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.logging.Logger;

public class ZookeeperService {

    Logger LOG = Logger.getLogger(ZookeeperService.class.getName());

    private static final String ZK_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;

    private static final String AUTOHEALER_ZNODES_PATH = "/throughput";

    private ZooKeeper zooKeeper;

    public ZookeeperService() throws IOException {
        zooKeeper = new ZooKeeper(ZK_ADDRESS, SESSION_TIMEOUT, e -> {});
    }

    public void addNodeToCluster() throws InterruptedException, KeeperException {
        zooKeeper.create(AUTOHEALER_ZNODES_PATH + "/throughput_",
                new byte[]{},
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
    }
}
