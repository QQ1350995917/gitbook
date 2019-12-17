package pwd.mysql.rw;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * pwd.mysql.rw@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-17 18:52
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class DBContextHolder {
  private static final ThreadLocal<DBTypeEnum> contextHolder = new ThreadLocal<>();

  private static final AtomicInteger counter = new AtomicInteger(-1);

  public static void set(DBTypeEnum dbType) {
    contextHolder.set(dbType);
  }

  public static DBTypeEnum get() {
    return contextHolder.get();
  }

  public static void master() {
    set(DBTypeEnum.MASTER);
    System.out.println("切换到master");
  }

  public static void slave() {
    //  轮询
    int index = counter.getAndIncrement() % 2;
    if (counter.get() > 9999) {
      counter.set(-1);
    }
    if (index == 0) {
      set(DBTypeEnum.SLAVE1);
      System.out.println("切换到slave1");
    }else {
      set(DBTypeEnum.SLAVE2);
      System.out.println("切换到slave2");
    }
  }
}
