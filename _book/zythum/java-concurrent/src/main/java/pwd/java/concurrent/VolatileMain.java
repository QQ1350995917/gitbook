package pwd.java.concurrent;

/**
 * pwd.java.concurrent@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-23 11:31
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class VolatileMain {

  volatile boolean stop = false;

  public static void main(String[] args) throws InterruptedException {
    VolatileMain volatileMain = new VolatileMain();
    new Thread(() -> {
      volatileMain.doWork(); //先就开始工作，stop为false
    }).start();
    Thread.sleep(1000);
    new Thread(() -> {
      volatileMain.shutDown(); //调用shtdown方法stop为true 但是线程工作副本的问题
    }).start();

  }

  public void shutDown() {
    stop = true;
  }

  public void doWork() {
    while (!stop) {
//      System.out.println("while");
    }
    System.out.println("over...");

  }
}
