package pwd.java.thread;

/**
 * pwd.java.thread@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-13 10:30
 *
 * @author DingPengwei[dingpengwei@eversec.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class Start {

  public static void main(String[] args) {
    Thread thread = new Thread(() -> System.out.println("run on " + Thread.currentThread().getName()));
    thread.start();
    thread.start();
  }

//  Exception in thread "main" run on Thread-0
//  java.lang.IllegalThreadStateException
//  at java.lang.Thread.start(Thread.java:708)
//  at pwd.java.thread.Start.main(Start.java:19)
}
