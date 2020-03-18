package pwd.java.zk.api;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;


/**
 * pwd.java.lambda@gitbook
 *
 * <h1>创建znode并监听事件</h1>
 *
 * date 2020-03-17 17:34
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ZKCreateNode implements Watcher {

  private static final CountDownLatch cdl = new CountDownLatch(1);

  public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
    ZooKeeper zk = new ZooKeeper("localhost:2181", 5000, new ZKCreateNode());
    cdl.await();

    String path1 = zk.create("/zk-test", "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    System.out.println("Success create path: " + path1);
    String path2 = zk.create("/zk-test", "456".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    System.out.println("Success create path: " + path2);

    Thread.sleep(Integer.MAX_VALUE);
  }




  //监听到事件时进行处理
  public void process(WatchedEvent event) {
    System.out.println("Receive watched event:" + event);
    if (KeeperState.SyncConnected == event.getState()) {
      cdl.countDown();
    }
  }

}
