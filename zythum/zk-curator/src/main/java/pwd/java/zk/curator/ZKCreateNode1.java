package pwd.java.zk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>curator递归创建顺序节点</h1>
 *
 * date 2020-03-17 17:34
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ZKCreateNode1 {


  public static void main(String[] args) throws Exception {
    String path = "/zk-curator/c1";
    CuratorFramework client = CuratorFrameworkFactory.builder().connectString("localhost:2181")
        .sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
    client.start();
    client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, "test".getBytes());
  }
}
