package pwd.java.concurrent.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * pwd.java.concurrent.pool@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-23 11:21
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ThreadPoolException {

  static class TaskRunnable implements Runnable {

    @Override
    public void run() {
      System.out.println("ok");
      throw new RuntimeException();
    }
  }

  public static void main(String[] args) throws Exception {
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(3);
    for (int i=0;i<5;i++) {
      scheduledThreadPool.execute(new TaskRunnable());
    }

    scheduledThreadPool.scheduleAtFixedRate(new TaskRunnable(),0,1, TimeUnit.SECONDS);

    Thread.sleep(3000);

    for (int i=0;i<5;i++) {
      scheduledThreadPool.execute(new TaskRunnable());
    }

    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
    for (int i=0;i<5;i++) {
      fixedThreadPool.execute(new TaskRunnable());
    }

  }
}
