package com.anzaiyun.watchServer;

import org.apache.zookeeper.*;

import java.io.IOException;

public class DistributeServer {
    // 注意：逗号前后不能有空格
    private static String connectString =  "192.168.56.11:2181,192.168.56.12:2181,192.168.56.13:2181";
    private static int sessionTimeout = 200000;
    private ZooKeeper zk = null;
    private String parentNode = "/servers";

    // 创建到zk的客户端链接
    public void getConnect() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }

    // 注册服务器
    public void registServer(String hostname) throws KeeperException, InterruptedException {
        String create = zk.create(parentNode + "/server", hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname+" is online "+ create);
    }

    //业务功能
    public void business(String hostname) throws InterruptedException {
        System.out.println(hostname + " is working ...");
        Thread.sleep(Long.MAX_VALUE);
    }

    public static void main(String[] args) throws Exception {
        // 1 获取 zk 连接
        DistributeServer server = new DistributeServer();
        server.getConnect();
        // 2 利用 zk 连接注册服务器信息
        server.registServer(args[0]);
        // 3 启动业务功能
        server.business(args[0]);
    }
}
