import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * PACKAGE_NAME@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-03-05 10:39
 *
 * @author DingPengwei[dingpengwei@eversec.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class Test {

  static CopyOnWriteArrayList<Integer> vector = new CopyOnWriteArrayList<Integer>();

  public static void main(String[] args) throws InterruptedException {
    HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
    Object key = objectObjectHashMap.put(null, 1);
    Object key1 = objectObjectHashMap.put(null, 2);
    System.out.println();
//    while (true) {
//      for (int i = 0; i < 10; i++) {
//        vector.add(i);
//      }
//
//      Thread thread1 = new Thread() {
//        public void run() {
//          for (int i = 0; i < vector.size(); i++) {
//            vector.remove(i);
//          }
//        }
//
//        ;
//      };
//      Thread thread2 = new Thread() {
//        public void run() {
//          for (int i = 0; i < vector.size(); i++) {
//            vector.get(i);
//          }
//        }
//      };
//      thread1.start();
//      thread2.start();
//      while (Thread.activeCount() > 10) {
//        System.out.println("Thread.activeCount() = " + Thread.activeCount());
//      }
//    }
  }
}


