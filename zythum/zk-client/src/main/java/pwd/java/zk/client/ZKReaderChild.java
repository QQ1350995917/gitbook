package pwd.java.zk.client;

import java.util.List;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>ZkClient获取子节点数据并监听事件</h1>
 *
 * date 2020-03-17 17:34
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ZKReaderChild {


  public static void main(String[] args) throws InterruptedException {
    String path = "/zk-client";
    ZkClient client = new ZkClient("localhost:2181", 5000);
    //注册子节点数据改变的事件
    client.subscribeChildChanges(path, new IZkChildListener() {

      //子节点数据改变事件
      public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        System.out.println(parentPath + "的子发生变化: " + currentChilds);
      }
    });

    //创建顺序节点
    client.createPersistent(path);
    Thread.sleep(1000);
    //获取子节点数据 此时还没有创建获取不到
    System.out.println(client.getChildren(path));
    //在前面的父节点 /zk-client下创建子节点c1
    client.createPersistent(path + "/c1");
    Thread.sleep(1000);
    //删除子节点
    client.delete(path + "/c1");
    Thread.sleep(1000);
    //删除父节点
    client.delete(path);
    Thread.sleep(Integer.MAX_VALUE);
  }
}
