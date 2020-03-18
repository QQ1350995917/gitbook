package pwd.java.zk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>curator事件监听</h1>
 *
 * date 2020-03-17 17:34
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ZKWatcher {


  public static void main(String[] args) throws Exception {
    String path = "/zk-curator/nodecache";
    CuratorFramework client = CuratorFrameworkFactory.builder().connectString("localhost:2181")
        .sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
    client.start();
    client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "test".getBytes());

    final NodeCache nc = new NodeCache(client, path, false);
    nc.start();
    //通过回调函数监听事件
    nc.getListenable().addListener(new NodeCacheListener() {

      public void nodeChanged() throws Exception {
        System.out.println("update--current data: " + new String(nc.getCurrentData().getData()));
      }
    });

    client.setData().forPath(path, "test123".getBytes());
    Thread.sleep(1000);
    client.delete().deletingChildrenIfNeeded().forPath(path);
    Thread.sleep(5000);
    nc.close();
  }

}
