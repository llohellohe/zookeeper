package yangqi.code;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException
    {
        ZooKeeper zk = new ZooKeeper("localhost:2181", 3000, new Watcher() {

            @Override
            public void process(WatchedEvent event) {
                System.out.println(event);

            }

        });

        zk.setData("/yangqi_test", "Data of node 3".getBytes(), -1);
    }

}
