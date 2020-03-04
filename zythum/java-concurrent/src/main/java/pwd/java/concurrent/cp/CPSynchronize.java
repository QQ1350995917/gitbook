package pwd.java.concurrent.cp;

/**
 * pwd.java.concurrent.cp@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-01-10 19:01
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class CPSynchronize {

  public static void main(String[] args) {
    Object locker = new Object();
    new Thread(new Product(locker)).start();
    new Thread(new Consume(locker)).start();
  }
}

class Product implements Runnable {

  Object locker;

  public Product(Object locker) {
    this.locker = locker;
  }

  @Override
  public void run() {
    while (true) {
      synchronized (locker) {
        try {
          System.out.println("product");
          Thread.sleep(1000);
          locker.notify();
          locker.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}

class Consume implements Runnable {

  Object locker;

  public Consume(Object locker) {
    this.locker = locker;
  }

  @Override
  public void run() {
    while (true) {
      synchronized (locker) {
        try {
          System.out.println("consumer");
          Thread.sleep(3000);
          locker.notify();
          locker.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

  }
}
