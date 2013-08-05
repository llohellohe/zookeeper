/*
 * Copyright 1999-2010 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package yangqi.code;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * 类Executor.java的实现描述：TODO 类实现描述
 * 
 * @author yangqi 2013-5-30 下午10:48:09
 */

public class Executor implements Watcher, Runnable, DataMonitorListener {

    String      znode;

    DataMonitor dm;

    ZooKeeper   zk;

    String      exec[];

    Process     child;

    public Executor(String hostPort, String znode, String exec[]) throws KeeperException, IOException {
        this.exec = exec;
        zk = new ZooKeeper(hostPort, 3000, this);
        dm = new DataMonitor(zk, znode, null, this);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        args = new String[] { "localhost:2181", "/yangqi_test" };

        String hostPort = args[0];
        String znode = args[1];
        String exec[] = new String[] { "date" };
        try {
            new Executor(hostPort, znode, exec).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************************************************
     * We do process any events ourselves, we just need to forward them on.
     * 
     * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.proto.WatcherEvent)
     */
    public void process(WatchedEvent event) {
        dm.process(event);
    }

    public void run() {
        try {
            synchronized (this) {
                while (!dm.dead) {
                    System.out.println("===========EXECUTOR START TO WAIT===========");
                    wait();
                    System.out.println("===========EXECUTOR STOP WAIT===========");
                }
            }
        } catch (InterruptedException e) {
        }
    }

    public void closing(int rc) {
        synchronized (this) {
            System.out.println("===========EXECUTOR START TO NOTIFY ALL===========");
            notifyAll();
            System.out.println("===========EXECUTOR START TO NOTIFY ALL===========");
        }
    }

    static class StreamWriter extends Thread {

        OutputStream os;

        InputStream  is;

        StreamWriter(InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
            start();
        }

        public void run() {
            byte b[] = new byte[80];
            int rc;
            try {
                System.out.println("===========START TO WRITE===========");
                while ((rc = is.read(b)) > 0) {
                    os.write(b, 0, rc);
                }
                System.out.println("===========STOP TO WRITE===========");
            } catch (IOException e) {
            }

        }
    }

    public void exists(byte[] data) {
        if (data == null) {
            if (child != null) {
                System.out.println("Killing process");
                child.destroy();
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                }
            }
            child = null;
        } else {
            if (child != null) {
                System.out.println("Stopping child");
                child.destroy();
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("===SHOW DATA===");
            System.out.println(new String(data));
            try {
                System.out.println("Starting child");
                child = Runtime.getRuntime().exec(exec);
                new StreamWriter(child.getInputStream(), System.out);
                new StreamWriter(child.getErrorStream(), System.err);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
