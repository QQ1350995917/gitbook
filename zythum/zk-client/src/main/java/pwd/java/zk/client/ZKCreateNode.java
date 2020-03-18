package pwd.java.zk.client;

import org.I0Itec.zkclient.ZkClient;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>ZkClient递归创建顺序节点</h1>
 *
 * date 2020-03-17 17:34
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ZKCreateNode {


  public static void main(String[] args) {
    ZkClient client = new ZkClient("localhost:2181", 5000);
    String path = "/zk-client/c1";
    // 递归创建顺序节点 true：先创建父节点/zk-client
    client.createPersistent(path, true);
  }
}
