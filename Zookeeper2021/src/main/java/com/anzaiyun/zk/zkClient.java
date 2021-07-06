package com.anzaiyun.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class zkClient {
    // 注意：逗号前后不能有空格
    private static String connectString =  "192.168.56.11:2181,192.168.56.12:2181,192.168.56.13:2181";
    //需要设一个大一点的值，不然因为网络通信的原因，没有在心跳时间内创建完客户端，会造成后续操作报错
    //org.apache.zookeeper.KeeperException$ConnectionLossException: KeeperErrorCode = ConnectionLoss
    private static int sessionTimeout = 100000;
    private ZooKeeper zkClient = null;

    @Before
    public void init() throws Exception{
        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                // 收到事件通知后的回调函数，用户的业务处理逻辑
                System.out.println(watchedEvent.getType() + "--" + watchedEvent.getPath());

                // 再次启动监听
                try{
                    List<String>  children = zkClient.getChildren("/", true);
                    for(String child:children){
                        System.out.println("watch:"+child);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    // 创建子节点
    @Test
    public void create() throws Exception {

    // 参数 1：要创建的节点的路径； 参数 2：节点数据 ； 参数 3：节点权限 ； 参数 4：节点的类型
        String nodeCreated = zkClient.create("/atguigu", "shuaige".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    // 获取子节点
    @Test
    public void getChildren() throws Exception {
        List<String> children = zkClient.getChildren("/", true);

        for (String child : children) {   System.out.println("value:"+child);  }

        // 延时阻塞
//        Thread.sleep(Long.MAX_VALUE);
    }

    //判断节点是否存在
    @Test
    public void exist() throws Exception{
        Stat stat = zkClient.exists("/atguigu", false);
        System.out.println(stat==null?"not exist":"exist");
    }
}
