package pwd.java.jvm.gc;

/**
 * pwd.java.jvm.gc@gitbook
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

  private byte[] bytes ;

  public MockOnlineEntity() {
    bytes = new byte[1024 * 1];
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    System.out.println(this.getClass().getName() + " finalize ");
  }

  public byte[] getBytes() {
    return bytes;
  }
}
