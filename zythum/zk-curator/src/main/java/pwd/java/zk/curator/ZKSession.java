package pwd.java.zk.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>curator创建连接session</h1>
 *
 * date 2020-03-17 17:34
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ZKSession {

  /**
   * 这里介绍一种算法：Backoff退避算法
   *
   * 有这样一种场景，有多个请求，如果网络出现阻塞,每1分钟重试一次。
   * 20：25 request1（block）
   * 20：26 request2（block）
   * 20：27 request3（block）
   * 当网络通顺的时候，请求都累在一起来发送
   * 20：28 request4（通顺）request2、3、4
   * 那么前面的请求就没有意义了，所以就有了退避算法，按照指数间隔重试，比如第一次1分钟，第二次2分钟......随着时间的推移，重试间隔越长。
   */
  public static void main(String[] args) throws InterruptedException {
    RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
    CuratorFramework client = CuratorFrameworkFactory.builder().connectString("localhost:2181")
        .sessionTimeoutMs(5000).retryPolicy(policy).build();
    client.start();
    Thread.sleep(Integer.MAX_VALUE);
  }

}
