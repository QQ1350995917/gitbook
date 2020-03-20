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


  public static void main(String[] args) {
//    for (int i = 0; i < 1; i++) {
//      new Thread(() -> {
//
//        while (true) {
//          List<byte[]> list = new LinkedList<>();
//          while (list.size() < 10) {
//            list.add(new byte[1024 * 1024]);
//            try {
//              Thread.sleep(1000);
//            } catch (InterruptedException e) {
//              e.printStackTrace();
//            }
//          }
//        }
//      }).start();
//    }
    List<MockOnlineEntity> list = new LinkedList<>();
    while (true) {
      while (list.size() < 10) {
        list.add(new MockOnlineEntity());
        try {
//          Thread.sleep(10);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

}
