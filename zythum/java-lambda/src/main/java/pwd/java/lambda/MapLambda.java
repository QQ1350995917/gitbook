package pwd.java.lambda;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-03-24 16:50
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class MapLambda {

  public static void main(String[] args) {
//    List<UserEntity> instances0 = UserEntity.getInstances0();
//
//    System.out.println("总人数：" + instances0.stream().mapToLong(user -> user.getId()).count());
//    System.out.println("总年龄：" + instances0.stream().mapToLong(user -> user.getAge()).sum());
//    System.out.println("平均年龄：" + instances0.stream().mapToDouble(user -> user.getAge()).average().getAsDouble());
//    System.out.println("大于19的总人数：" + instances0.stream().filter((user) -> user.getAge() > 19).mapToLong(user -> user.getId()).count());
//    System.out.println("大于19的详细信息：");
//    instances0.stream().filter((user) -> user.getAge() > 19).forEach(System.out::println);
//    System.out.println("大于19小于22的总人数：" + instances0.stream().filter((user) -> user.getAge() > 19).filter((user) -> user.getAge() < 22).mapToLong(user -> user.getId()).count());
//    System.out.println("大于19小于22的详细信息：");
//    instances0.stream().filter((user) -> user.getAge() > 19).filter((user) -> user.getAge() < 22).forEach(System.out::println);
//    System.out.println("提取名称列表：");
//    instances0.stream().map(a-> a.getName()).collect(Collectors.toList()).forEach(System.out::println);

    HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
    HashMap<Object, Object> subObjectObjectHashMap = new HashMap<>();
    LinkedList<Object> listInSubMap = new LinkedList<>();
    Map<String,String> entity = new HashMap<>();
    entity.put("name","pwd");
    entity.put("age","18");
    entity.put("gender","1");
    listInSubMap.add(entity);
    listInSubMap.add(entity);
    listInSubMap.add(entity);
    subObjectObjectHashMap.put("list",listInSubMap);
    objectObjectHashMap.put("map0",subObjectObjectHashMap);
    objectObjectHashMap.put("map1",subObjectObjectHashMap);
    objectObjectHashMap.put("map2",subObjectObjectHashMap);
    objectObjectHashMap.put("list3",listInSubMap);
    objectObjectHashMap.put("name","pwd");
    objectObjectHashMap.put("age","18");
    Stream.of(objectObjectHashMap)
        .forEach(System.out::print);
  }
}
