package pwd.java.gc;

import java.util.LinkedList;
import java.util.List;

/**
 * pwd.java.gc@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-03-20 14:00
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class MockOnlineEntity {

  private byte[] bytes = new byte[1024 * 1024 * 2];

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    System.out.println(this.getClass().getName() + " finalize ");
  }
}
