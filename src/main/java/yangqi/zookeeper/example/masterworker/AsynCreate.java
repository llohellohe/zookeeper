/*
 * Copyright 1999-2010 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package yangqi.zookeeper.example.masterworker;

import java.io.IOException;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 类AsynCreate.java的实现描述：TODO 类实现描述 
 * @author yangqi Jan 2, 2014 9:39:02 PM
 */
public class AsynCreate {

    /**
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zookeeper = new ZooKeeper("localhost:2181", 200000, null);

        zookeeper.create("/mas", "sid-o2".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                         new StringCallback() {

            @Override
            public void processResult(int rc, String path, Object ctx, String name) {
                Code code = Code.get(rc);
                switch (code) {
                    case OK:
                        System.out.println(code);
                        break;
                    case NODEEXISTS:
                        System.out.println(code);
                        break;
                    case SESSIONEXPIRED:
                        System.out.println(code);
                        break;
                    default:
                        System.out.println("unknow " + code);
                }

            }
        }, null);

        DataCallback callback = new DataCallback() {

            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                Code code = Code.get(rc);
                System.out.println("code for check " + code);
                switch (code) {
                    case OK:
                        break;
                    case NONODE:
                        break;
                    case NODEEXISTS:
                        break;
                    case SESSIONEXPIRED:
                        break;
                    default:
                }

            }

        };

        zookeeper.getData("/mas", true, callback, null);

        Thread.sleep(200000);

    }

}
