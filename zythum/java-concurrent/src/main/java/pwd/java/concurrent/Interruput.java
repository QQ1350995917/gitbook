package pwd.java.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * pwd.java.concurrent@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-24 17:56
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class Interruput {
  public static void main(String[] args) throws InterruptedException {
    Thread t1 = new Thread() {
      @Override
      public void run() {
        //while在try中，通过异常中断就可以退出run循环
        try {
          while (true) {
            //当前线程处于阻塞状态，异常必须捕捉处理，无法往外抛出
            TimeUnit.SECONDS.sleep(2);
            System.out.println("--");
          }
        } catch (InterruptedException e) {
          System.out.println("Interruted When Sleep");
          boolean interrupt = this.isInterrupted();
          //中断状态被复位
          System.out.println("interrupt:"+interrupt);
        }
      }
    };
    t1.start();
    TimeUnit.SECONDS.sleep(2);
    //中断处于阻塞状态的线程
    t1.interrupt();

    /**
     * 输出结果:
     Interruted When Sleep
     interrupt:false
     */
  }
}
