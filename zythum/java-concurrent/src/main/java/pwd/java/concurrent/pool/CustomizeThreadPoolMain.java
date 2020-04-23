package pwd.java.concurrent.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * pwd.java.concurrent.pool@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-04-20 17:37
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class CustomizeThreadPoolMain {

  public static void main(String[] args) throws Exception {

    CustomizeThreadPool customizeThreadPool = CustomizeThreadPool.newFixedThreadPool(1);
    customizeThreadPool.execute(() -> {
      Thread.currentThread().interrupt();
      System.out.println("task 1 " + Thread.currentThread().getId());

      Thread.currentThread().interrupt();
      if (Thread.currentThread().isInterrupted()) {
        System.out.println("task 1 interrupted");
      } else {
        System.out.println("task 1 not interrupted");
      }
    });

    Thread.sleep(2 * 1000);

    customizeThreadPool.execute(() -> {
      try {
        Thread.sleep(1 * 1000);
        System.out.println("task 2 " + Thread.currentThread().getId());
        if (Thread.currentThread().isInterrupted()) {
          System.out.println("task 2 interrupted");
        } else {
          System.out.println("task 2 not interrupted");
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }
}

class CustomizeThreadPool extends ThreadPoolExecutor {

  public CustomizeThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      TimeUnit unit,
      BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
  }

  public static CustomizeThreadPool newFixedThreadPool(int nThreads) {
    return new CustomizeThreadPool(nThreads, nThreads,
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>());
  }


  @Override
  protected void beforeExecute(final Thread t, final Runnable r) {

  }

  @Override
  protected void afterExecute(final Runnable r, final Throwable t) {

  }

}
