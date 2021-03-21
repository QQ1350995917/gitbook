package pwd.java.collections;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * pwd.java.collections@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2021-03-21 11:30
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class CopyOnWriteArrayListTest {


  public static void main(String[] args) {
    CopyOnWriteArrayList list = new CopyOnWriteArrayList();

    System.out.println(list);

    list.add(new Object());

    System.out.println(list);

    list.add(new Object());

    System.out.println(list);

  }

}
