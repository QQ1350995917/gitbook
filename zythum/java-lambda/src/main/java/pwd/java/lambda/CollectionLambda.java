package pwd.java.lambda;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.plaf.synth.SynthOptionPaneUI;

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
public class CollectionLambda {



  public static void main(String[] args) {
    Integer[] integers = {1, 1, 0, 2, 3, 4, 0, 5, 6, 7, 8, 9, 10};
    List<Integer> nums0 = Arrays.asList(integers);
    System.out.println("sum is:"+nums0.stream().filter(num -> num > 0).distinct().mapToInt(num -> num * 2).skip(2).limit(4).peek(System.out::println).sum());

    List<Integer> nums1 = Arrays.asList(integers);
    List<Integer> numsWithoutNull = nums1.stream().filter(num -> num > 0).
        collect(() -> new ArrayList<Integer>(), (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
  }

}
