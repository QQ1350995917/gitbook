package pwd.java.gc;

import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

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

  public static void main(String[] args) throws Exception {
    Thread.currentThread().setName("MockOnline");
    new MockOnline().test();

    new CountDownLatch(1).await();

    System.out.println("over");

  }
  static List<Object> list = new LinkedList<>();
  public void test(){

    String name = ManagementFactory.getRuntimeMXBean().getName();
    System.out.println(name);
    String pid = name.split("@")[0];
    System.out.println("Pid is:" + pid);
    new Scanner(System.in).nextLine();

    for (int i =0 ;i<1;i++) {
      new Thread(()->{
//        while (true) {
          list.add(new MockEntityCapacity());
          try {
//          System.out.println("---" + System.currentTimeMillis());
            Thread.sleep(100);
          } catch (Exception e) {
            e.printStackTrace();
          }
//        }
      }).start();
    }
  }
}
