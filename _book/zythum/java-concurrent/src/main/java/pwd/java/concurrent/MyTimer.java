package pwd.java.concurrent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * pwd.java.concurrent@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-01-07 12:12
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class MyTimer {

  public static void main(String[] args) {
//    schedule();
    scheduleAtFixedRate();
  }


  static void schedule(){
    Timer testTimer = new Timer("testTimer");
    testTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        System.out.println("TimerTask1");
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }, 0,1000);

    testTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        System.out.println("TimerTask2");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }, 0,1000);
  }

  static void scheduleAtFixedRate(){
    Timer testTimer = new Timer("testTimer");
    testTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        System.out.println("TimerTask1");
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }, 0,1000);

    testTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        System.out.println("TimerTask2");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }, 0,1000);
  }
}
