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

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 类Master.java的实现描述：TODO 类实现描述 
 * @author yangqi Jan 1, 2014 1:37:01 PM
 */
public class AsynMaster implements Watcher, Runnable {

    private ZooKeeper zk;

    private String    connectString;

    private String    serverId;

    private static final String MASTER_PATH = "/master";

    public AsynMaster(String connectString,String serverId) {
        this.connectString = connectString;
        this.serverId = serverId;
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
    }

    public void startZK() {
        try {
            zk = new ZooKeeper(connectString, 2000, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopZK() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createMaterNode(){
        String ctx = "ctx for " + serverId;
        zk.create(MASTER_PATH, serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                      new StringCallback() {
                          @Override
                          public void processResult(int rc, String path, Object ctx, String name) {
                          Code code = Code.get(rc);
                          switch (code) {
                              case OK:
                                  log("create master ok");
                                  sleep(10);
                                  stopZK();
                                  break;
                              case NODEEXISTS:
                                  log("node exists");
                                  checkForMaster();
                                  break;
                              case SESSIONEXPIRED:
                                  log("session expired in create");
                                  sleep(10);
                                  break;
                              default:
                                  checkForMaster();
                                  log("code is " + code);
                          }

                          }
                  }, ctx);
    }

    public void checkForMaster() {
        DataCallback callback = new DataCallback() {

            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                Code code = Code.get(rc);
                switch (code) {
                    case OK:
                        if (new String(data).equals(serverId)) {
                            System.out.println("stop now");
                            stopZK();
                        } else {
                            checkForMaster();
                        }
                        break;
                    case NONODE:
                        log("node not exists");
                        createMaterNode();
                        break;
                    case NODEEXISTS:
                        log("node exists");
                        createMaterNode();
                        break;
                    case SESSIONEXPIRED:
                        log("session expired in check");
                        sleep(10);
                        break;
                    default:
                        log("code is " + code);
                        checkForMaster();
                }

            }

        };

        zk.getData(MASTER_PATH, true, callback, null);

    }

    public void registerForMaster() {
        checkForMaster();
    }


    @Override
    public void run() {

        startZK();

        registerForMaster();


    }

    private static void sleep(int seconds) {
        try {
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void log(String msg) {
        System.out.println(String.format("serverId %s %s", serverId, msg));
    }

    public static void main(String[] args) throws InterruptedException {

        int masterCount = 3;
        ExecutorService service = Executors.newFixedThreadPool(masterCount);
        for (int i = 0; i < masterCount; i++) {
            AsynMaster master = new AsynMaster("localhost:2181", "o2-" + i);
            service.submit(master);
        }

        sleep(1000);

    }
}
