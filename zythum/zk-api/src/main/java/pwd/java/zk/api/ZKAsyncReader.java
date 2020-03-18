package pwd.java.zk.api;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>连接后创建回调</h1>
 *
 * date 2020-03-17 17:34
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
//异步调用并完成回调
class ZKAsyncReaderChildrenCallback implements AsyncCallback.Children2Callback {

  public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
    System.out.println(
        "Child: " + rc + ", path: " + path + ", ctx: " + ctx + ", children: " + children
            + ", stat: " + stat);
  }
}

public class ZKAsyncReader implements Watcher {

  private static final CountDownLatch cdl = new CountDownLatch(1);
  private static ZooKeeper zk = null;

  public static void main(String[] args) throws Exception {
    zk = new ZooKeeper("192.168.152.130:2181", 5000, new ZKAsyncReader());
    cdl.await();

    zk.create("/zk-test", "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

    zk.create("/zk-test/c1", "456".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

    zk.getChildren("/zk-test", true, new ZKAsyncReaderChildrenCallback(), "ok");

    Thread.sleep(Integer.MAX_VALUE);
  }


  //监听到事件时进行处理
  public void process(WatchedEvent event) {
    if (KeeperState.SyncConnected == event.getState()) {
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

}
