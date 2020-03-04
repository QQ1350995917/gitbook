package pwd.java.thread;

/**
 * pwd.java.thread@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-13 10:22
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class Run {

  public static void main(String[] args) throws InterruptedException {
    Thread thread = new Thread(() -> System.out.println("run on " + Thread.currentThread().getName()));

//    thread.start();
//    thread.run();
//
//    run on main
//    run on Thread-0
//
//    thread.run();
//    thread.start();
//
//    run on main
//    run on Thread-0
//
//    thread.start();
//    thread.join();
//    thread.run();
//
//    run on Thread-0
//
//    TODO why

  }
}
