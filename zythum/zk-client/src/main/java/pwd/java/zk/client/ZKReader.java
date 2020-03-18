package pwd.java.zk.client;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>ZkClient获取数据并监听事件</h1>
 *
 * date 2020-03-17 17:34
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ZKReader {


  public static void main(String[] args) throws InterruptedException {
    String path = "/zk-client";
    ZkClient client = new ZkClient("localhost:2181", 5000);
    //创建临时节点
    client.createEphemeral(path, "123");

    //注册父节点数据改变的事件
    client.subscribeDataChanges(path, new IZkDataListener() {

      //父节点数据改变事件
      public void handleDataChange(String dataPath, Object data) throws Exception {
        System.out.println(dataPath + " changed: " + data);
      }

      //父节点数据删除事件
      public void handleDataDeleted(String dataPath) throws Exception {
        System.out.println(dataPath + " deleted");
      }
    });

    System.out.println(client.readData(path).toString());
    client.writeData(path, "456");
    Thread.sleep(1000);
    client.delete(path);
    //sleep的目的是为了更好的观察事件变化
    Thread.sleep(Integer.MAX_VALUE);
  }
}
