package pwd.java.dubbo.consumer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-03-17 17:34
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class DubboConsumerApplication {

  private static List<Map<String, String>> list = new LinkedList<>();

  static {
    for (int i = 0; i < 5; i++) {
      HashMap<String, String> map = new HashMap<>();
      int anInt = new Random().nextInt(5);
      for (int j = 0; j < anInt; j++) {
        map.put("key_" + j, "value_" + j);
      }
      list.add(map);
    }
  }

  public static void main(String[] args) {
    list.stream().filter(map -> map.size() > 0).filter(map -> {
      map.forEach((key,value) -> {

      });
      return true;
    }
    ).forEach(map -> {

    });
  }

}
