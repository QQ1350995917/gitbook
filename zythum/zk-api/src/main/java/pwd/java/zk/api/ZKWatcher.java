package pwd.java.zk.api;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>连接zk并监听事件</h1>
 *
 * date 2020-03-17 17:34
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ZKWatcher implements Watcher {
  private static final CountDownLatch cdl = new CountDownLatch(1);

  public static void main(String[] args) throws IOException {
    ZooKeeper zk = new ZooKeeper("localhost:2181", 5000, new ZKWatcher());
    System.out.println(zk.getState());

    try {
      cdl.await();
    } catch (Exception e) {
      System.out.println("ZK Session established.");
    }
  }


  //监听到事件时进行处理
  public void process(WatchedEvent event) {
    System.out.println("Receive watched event:" + event);
    if (KeeperState.SyncConnected == event.getState()) {
      cdl.countDown();
    }
  }

}
