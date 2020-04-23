package pwd.java.lambda;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-04-20 17:13
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class PeekTest {

  public static void main(String[] args) {

    Stream.of("one", "two", "three", "four")
               .filter(e -> e.length() > 3)
               .peek(e -> System.out.println("Filtered value: " + e))
               .map(String::toUpperCase)
               .peek(e -> System.out.println("Mapped value: " + e))
               .collect(Collectors.toList());

    List<String> strings = Arrays.asList(new String[]{"one", "two", "three", "four"});
    Stream.of(strings).flatMap(obj -> obj.stream())
        .filter(e -> e.length() > 3)
               .peek(e -> System.out.println("Filtered value: " + e))
//        .map(String::toUpperCase)
        .peek(e -> System.out.println("Mapped value: " + e))
        .collect(Collectors.toList());

  }
}
