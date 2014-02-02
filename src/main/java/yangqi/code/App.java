package yangqi.code;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

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

        Stat e=zk.exists("/yangqi_test",null);

        System.out.println("exists "+e);

        zk.setData("/yangqi_test", "Data of node 3".getBytes(), -1);
    }

}
