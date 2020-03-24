package pwd.java.lambda;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-03-24 16:51
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

  private Long id;
  private String name;
  private Integer age;
  private Byte gender;
  private String summary;

  public static List<UserEntity> getInstances0 () {
    List<UserEntity> instances = new ArrayList<>(10);
    instances.add(new UserEntity(0L, "shenzhen0", 22, Byte.valueOf("0"), "hi"));
    instances.add(new UserEntity(1L, "shenzhen0", 19, Byte.valueOf("0"), "hello"));
    instances.add(new UserEntity(2L, "shenzhen0", 18, Byte.valueOf("0"), "fine"));
    instances.add(new UserEntity(3L, "shanghai0", 18, Byte.valueOf("0"), "ok"));
    instances.add(new UserEntity(4L, "shanghai0", 18, Byte.valueOf("0"), "ok"));
    instances.add(new UserEntity(5L, "shagnhai0", 20, Byte.valueOf("0"), "ok"));
    instances.add(new UserEntity(6L, "shanghai0", 22, Byte.valueOf("0"), "fine"));
    instances.add(new UserEntity(7L, "zhuozhou0", 18, Byte.valueOf("0"), "hello"));
    instances.add(new UserEntity(8L, "zhuozhou0", 19, Byte.valueOf("0"), "hi"));
    instances.add(new UserEntity(9L, "shenzhen0", 20, Byte.valueOf("0"), "hi"));
    return instances;
  }


  public static List<UserEntity> getInstances1 () {
    List<UserEntity> instances = new ArrayList<>(10);
    instances.add(new UserEntity(10L, "shenzhen1", 22, Byte.valueOf("1"), "hi"));
    instances.add(new UserEntity(11L, "shenzhen1", 19, Byte.valueOf("1"), "hello"));
    instances.add(new UserEntity(12L, "shenzhen1", 18, Byte.valueOf("1"), "fine"));
    instances.add(new UserEntity(13L, "shanghai1", 18, Byte.valueOf("1"), "ok"));
    instances.add(new UserEntity(14L, "shanghai1", 18, Byte.valueOf("1"), "ok"));
    instances.add(new UserEntity(15L, "shagnhai1", 20, Byte.valueOf("1"), "ok"));
    instances.add(new UserEntity(16L, "shanghai1", 22, Byte.valueOf("1"), "fine"));
    instances.add(new UserEntity(17L, "zhuozhou1", 18, Byte.valueOf("1"), "hello"));
    instances.add(new UserEntity(18L, "zhuozhou1", 19, Byte.valueOf("1"), "hi"));
    instances.add(new UserEntity(19L, "shenzhen1", 20, Byte.valueOf("1"), "hi"));
    return instances;
  }
}
