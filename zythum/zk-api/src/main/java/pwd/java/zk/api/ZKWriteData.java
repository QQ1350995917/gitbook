package pwd.java.zk.api;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
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
 * <h1>改变znode数据并监听事件</h1>
 *
 * date 2020-03-17 17:34
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ZKWriteData implements Watcher {

  private static final CountDownLatch cdl = new CountDownLatch(1);
  private static ZooKeeper zk = null;
  private static Stat stat = new Stat();

  public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
    zk = new ZooKeeper("localhost:2181", 5000, new ZKWriteData());
    cdl.await();

    zk.create("/zk-test", "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    System.out.println(new String(zk.getData("/zk-test", true, stat)));

    zk.getData("/zk-test", true, stat);
    System.out.println(stat.getCzxid() + ", " + stat.getMzxid() + ", " + stat.getVersion());
    zk.setData("/zk-test", "123".getBytes(), -1);

    Thread.sleep(Integer.MAX_VALUE);
  }

  //监听到事件时进行处理
  public void process(WatchedEvent event) {
    if (KeeperState.SyncConnected == event.getState()) {
      if (EventType.None == event.getType() && null == event.getPath()) {
        cdl.countDown();
      } else if (event.getType() == EventType.NodeDataChanged) {
        try {
          System.out.println(new String(zk.getData(event.getPath(), true, stat)));
          System.out.println(stat.getCzxid() + ", " + stat.getMzxid() + ", " + stat.getVersion());
        } catch (Exception e) {
        }
      }
    }
  }

}
