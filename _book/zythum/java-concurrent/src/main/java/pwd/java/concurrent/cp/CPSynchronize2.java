package pwd.java.concurrent.cp;

import java.util.LinkedList;

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
public class CPSynchronize2 {

  public static void main(String[] args) {
    LinkedList<String> locker = new LinkedList<>();
    new Thread(new Product2(locker)).start();
    new Thread(new Consume2(locker)).start();
  }
}

class Product2 implements Runnable {

  LinkedList<String> locker;

  public Product2(LinkedList<String> locker) {
    this.locker = locker;
  }

  @Override
  public void run() {
    while (true) {
      synchronized (locker) {
        try {
          if (locker.size() <= 5) { // 单线程模式下可以使用if，多线程模式下使用while，否则会出现虚假唤醒
            System.out.println("product");
            Thread.sleep(1000);
            locker.add("");
            locker.add("");
            locker.add("");
            locker.add("");
            locker.add("");
            locker.add("");
          } else {
            locker.notify();
            locker.wait();
          }

        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}

class Consume2 implements Runnable {

  LinkedList<String> locker;

  public Consume2(LinkedList<String> locker) {
    this.locker = locker;
  }

  @Override
  public void run() {
    while (true) {
      synchronized (locker) {
        try {
          if (locker.size() > 5) { // 单线程模式下可以使用if，多线程模式下使用while，否则会出现虚假唤醒
            System.out.println("consumer");
            Thread.sleep(3000);
            locker.clear();
          } else {
            locker.notify();
            locker.wait();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

  }
}
