package pwd.java.thread.pool;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * pwd.java.thread.pool@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-11 18:24
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ThreadPoolMain {

  private static final ThreadPoolExecutor generalExecutorService = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());


  public static void main(String[] args) throws Exception {

    generalExecutorService.execute(() -> System.out.println("run on " + Thread.currentThread().getName()));
    generalExecutorService.execute(() -> System.out.println("run on " + Thread.currentThread().getName()));
    generalExecutorService.execute(() -> System.out.println("run on " + Thread.currentThread().getName()));
  }
}
