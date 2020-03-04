package pwd.java.concurrent.sync;

/**
 * pwd.java.concurrent.sync@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-24 14:04
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class Synchronized01 {
  public static void main(String[] args) {
    synchronized (Synchronized01.class) {
      System.out.println("main");
    }
    method();
  }
  public static synchronized void method() {
    System.out.println("method");
  }
}
