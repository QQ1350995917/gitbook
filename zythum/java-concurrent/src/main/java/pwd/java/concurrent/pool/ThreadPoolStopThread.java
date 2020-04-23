package pwd.java.concurrent.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * pwd.java.concurrent.pool@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-04-14 11:57
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ThreadPoolStopThread {

  private static final ThreadPoolExecutor executorService = (ThreadPoolExecutor)Executors.newFixedThreadPool(1);

  public static void main(String[] args) throws Exception {
    for (int i = 0; i < 5; i++) {
      run(i);
    }

    System.out.println("stop");
    Thread.sleep(1000);
    submit.cancel(true);

  }
  static Future<?> submit;
  private static void run(int i){

    if (i == 0) {
      submit = executorService.submit(() -> {
        try {
          System.out.println("start" + i + ":"+ Thread.currentThread().getName());
          Thread.sleep(1000 * 5);
        } catch (InterruptedException e) {
          System.out.println("stop " + i + ":"+ Thread.currentThread().getName());
          e.printStackTrace();
        }
      });
    } else {
      executorService.submit(() -> {
        try {
          System.out.println("start" + i + ":"+ Thread.currentThread().getName());
          Thread.sleep(1000 * 5);
        } catch (InterruptedException e) {
          System.out.println("stop " + i + ":"+ Thread.currentThread().getName());
          e.printStackTrace();
        }
      });
    }

    System.out.println(submit);
//    if (!submit.isDone() && !submit.isCancelled()){
//      submit.cancel(true);
//    }
  }

}
