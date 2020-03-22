package pwd.java.gc;

import java.util.LinkedList;
import java.util.List;

/**
 * pwd.java.gc@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-03-20 14:00
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class MockOnline {
  static List<Object> list = new LinkedList<>();
  public static void main(String[] args) {
    for (int i =0 ;i<2;i++) {
      new Thread(()->{
      while (true) {
        list.add(new MockOnlineEntity());
        try {
          System.out.println("---" + System.currentTimeMillis());
          Thread.sleep(2000);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      }).start();
    }

  }
}
