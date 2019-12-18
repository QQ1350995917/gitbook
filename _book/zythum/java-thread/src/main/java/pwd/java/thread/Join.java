package pwd.java.thread;

/**
 * pwd.java.thread@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-13 9:48
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class Join {


  public static void main(String[] args) throws InterruptedException {
    Thread thread0 = new Thread(() -> System.out.println("run on " + Thread.currentThread().getName()));
    Thread thread1 = new Thread(() -> System.out.println("run on " + Thread.currentThread().getName()));
    Thread thread2 = new Thread(() -> System.out.println("run on " + Thread.currentThread().getName()));

//    thread0.start();
//    thread0.join();
//    thread1.start();
//    thread1.join();
//    thread2.start();
//    thread2.join();
    //run on Thread-0
    //run on Thread-1
    //run on Thread-2

    thread0.start();
    thread1.start();
    thread2.start();

    thread0.join();
    thread1.join();
    thread2.join();
  }

}
