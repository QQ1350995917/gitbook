package pwd.java.gc;

/**
 * pwd.java.gc@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-03-13 18:14
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class GCByCountMain {

  public static void main(String[] args) {
    GCByCount1 gcByCount1 = new GCByCount1();
    GCByCount2 gcByCount2 = new GCByCount2();

    gcByCount1.setGcByCount2(gcByCount2);
    gcByCount2.setGcByCount1(gcByCount1);

    gcByCount1 = null;
    gcByCount2 = null;

  }
}
