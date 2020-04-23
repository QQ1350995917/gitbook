package pwd.java.concurrent.thread;

/**
 * pwd.java.concurrent.thread@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-04-14 17:23
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ThreadException {

  public static void main(String[] args) throws Exception {

    Thread thread = new Thread(() -> {
      for (int i = 0; i < 10; i++) {

        System.out.println(i);

        try {
          throw (Exception) new Throwable();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    thread.start();

    Thread.sleep(1000 * 3);
    System.out.println("==================================================");
    thread.start();
  }
}
