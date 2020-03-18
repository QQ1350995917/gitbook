package pwd.java.zk.api;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>改变子节点并监听事件</h1>
 *
 * date 2020-03-17 17:34
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ZKChildNodeWatcher implements Watcher {
  private static final CountDownLatch cdl = new CountDownLatch(1);
  private static ZooKeeper zk = null;

  public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
    zk = new ZooKeeper("localhost:2181", 5000, new ZKChildNodeWatcher());
    cdl.await();

    zk.create("/zk-test", "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

    zk.create("/zk-test/c1", "456".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

    List<String> list = zk.getChildren("/zk-test", true);
    for (String str : list)
      System.out.println(str);

    zk.create("/zk-test/c2", "789".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

    Thread.sleep(Integer.MAX_VALUE);
  }

  //监听到事件时进行处理
  public void process(WatchedEvent event) {
    if (KeeperState.SyncConnected == event.getState())
      if (EventType.None == event.getType() && null == event.getPath()) {
        cdl.countDown();
      } else if (event.getType() == EventType.NodeChildrenChanged) {
        try {
          System.out.println("Child: " + zk.getChildren(event.getPath(), true));
        } catch (Exception e) {
        }
      }
  }

}
