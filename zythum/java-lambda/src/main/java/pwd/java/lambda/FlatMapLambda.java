package pwd.java.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-03-24 17:16
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class FlatMapLambda {

  public static void main(String[] args) {
//    List<UserEntity> instances0 = UserEntity.getInstances0();
//    List<UserEntity> instances1 = UserEntity.getInstances1();
//    System.out.println("合并两个集合：");
//    Stream.of(instances0, instances1).flatMap(user -> user.subList(0, instances0.size() > instances1.size() ? instances1.size():instances0.size()).stream())
//        .collect(Collectors.toList()).forEach(System.out::println);

//    System.out.println("====");
//    instances0.stream().filter(user -> instances0.stream().map(UserEntity::getId).collect(Collectors.toList()).contains(user.getId())).collect(Collectors.toList()).forEach(System.out::println);

//    System.out.println("====");
//    instances0.stream().filter(user -> instances1.stream().map(UserEntity::getSummary).collect(Collectors.toList()).equals(user.getSummary())).collect(Collectors.toList());

//    System.out.println("====");
//    Stream.of(instances0).flatMap(user -> user.stream()).forEach(System.out::println);

//    System.out.println("糅合两个集合：");
//    instances0.stream()
//        .flatMap(user1 -> instances1.stream().filter(user2 -> user1.getSummary().equals(user2.getSummary()))
//            .map(user -> new UserEntity(user.getId(), user1.getName(), user.getAge(), user.getGender(), user1.getSummary())))
//        .collect(Collectors.toList()).forEach(System.out::println);

    String ss = "Hello";
    String[] aa = ss.split("");
    String[] bb = {"H", "e", "l", "l", "o"};
    String[] strings = {"Hello", "World"};

    //Arrays.stream接收一个数组返回一个流
    List<Stream<String>> streamList = Arrays.asList(strings).stream().
        map(str -> str.split("")).
        map(str -> Arrays.stream(str)).
        collect(Collectors.toList());

    //分步写(map)
    Stream<String[]> stream = Arrays.asList(strings).stream().
        map(str -> str.split(""));

    Stream<Stream<String>> streamStream = stream.map(strings1 -> Arrays.stream(strings1));
    List<Stream<String>> streamList1 = streamStream.collect(Collectors.toList());

    List<String> stringList = Arrays.asList(strings).stream().
        map(str -> str.split("")).
        flatMap(str -> Arrays.stream(str))
        .collect(Collectors.toList());

    //分步写(流只能消费一次)(flatMap)
    Stream<String[]> stream1 = Arrays.asList(strings).stream().
        map(str -> str.split(""));
    Stream<String> stringStream = stream1.flatMap(strings1 -> Arrays.stream(strings1));

    List<String> stringList1 = stringStream.collect(Collectors.toList());

  }
}
