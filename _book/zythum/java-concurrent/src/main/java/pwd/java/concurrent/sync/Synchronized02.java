package pwd.java.concurrent.sync;

import java.util.stream.IntStream;

/**
 * pwd.java.concurrent.sync@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-24 18:14
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */

public class Synchronized02 implements Runnable {

  static int i = 0;

  public static void main(String[] args) throws Exception {
    Synchronized02 sync01 = new Synchronized02();

//    IntStream.range(0, 2).forEach(index -> new Thread(sync01) {
//    }.start());

    Thread thread1 = new Thread(sync01);
    Thread thread2 = new Thread(sync01);
    Thread thread3 = new Thread(sync01);
    Thread thread4 = new Thread(sync01);
    Thread thread5 = new Thread(sync01);
    thread1.start();
    thread2.start();
    thread3.start();
    thread4.start();
    thread5.start();
    thread1.join();
    thread2.join();
    thread3.join();
    thread4.join();
    thread5.join();
    System.out.println(i);


  }

  @Override
  public void run() {
    add(0);
  }

  // 线程是否安全要根据调动者调用的是否是同一个对象
  private synchronized void add(Integer overloadFlag) {
    for (int j = 0; j < 10000; j++) {
      i++;
    }
  }

  // 线程是否安全要根据调动者调用的是否是同一个对象
  private void add(String overloadFlag) {
    synchronized (this) {
      for (int j = 0; j < 10000; j++) {
        i++;
      }
    }
  }

  // 线程是否安全要根据调动者调用的是否是同一个对象
  private void add(Long overloadFlag) {
    synchronized (Synchronized02.class) {
      for (int j = 0; j < 10000; j++) {
        i++;
      }
    }
  }
}
