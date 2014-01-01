/*
 * Copyright 1999-2010 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package yangqi.zookeeper.example.masterworker;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 类Master.java的实现描述：TODO 类实现描述 
 * @author yangqi Jan 1, 2014 1:37:01 PM
 */
public class Master implements Watcher, Runnable {

    private ZooKeeper zk;

    private String    connectString;

    private String    serverId;

    private static final String MASTER_PATH = "/master";

    public Master(String connectString,String serverId) {
        this.connectString = connectString;
        this.serverId = serverId;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
     */
    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
    }

    public void startZK() {
        try {
            zk = new ZooKeeper(connectString, 2000, this);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void stopZK() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createMaterNode(){
        String response = null;
        try {
            response = zk.create(MASTER_PATH, serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                                        CreateMode.EPHEMERAL);

        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(serverId + " response is " + response);
    }

    public boolean checkForMaster() {

        Stat stat=new Stat();
        byte[] data = null;
        try {
            data = zk.getData("/master", false, stat);
            System.out.println(serverId + " stat return " + new String(data));
            return serverId.equals(new String(data));
        } catch (KeeperException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Boolean.FALSE;
    }

    public boolean registerForMaster() {
        int count = 1;
        boolean isLeader = false;
        while (true) {
            System.out.println(serverId + " start to check count " + count++);
            if (!checkForMaster()) {
                createMaterNode();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                isLeader = true;
                System.out.println(serverId + " master registered with " + serverId);
                break;
            }
        }
        return isLeader;
    }

    public static void main(String[] args) throws InterruptedException {
        int masterCount = 3;
        ExecutorService service = Executors.newFixedThreadPool(masterCount);
        for (int i = 0; i < masterCount; i++) {
            Master master = new Master("localhost:2181", "o2-" + i);
            service.submit(master);
        }

    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            Thread.sleep(1000 * 1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startZK();

        boolean isLeader = registerForMaster();
        if (isLeader) {
            stopZK();
        } else {
        try {
                Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            stopZK();
        }

    }
}
