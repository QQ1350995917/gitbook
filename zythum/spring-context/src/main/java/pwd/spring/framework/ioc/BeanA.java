package pwd.spring.mybatis.ioc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * pwd.spring.mybatis.ioc@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-27 11:37
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BeanA {
  private Long id;
  private String name;
  private BeanB beanB;

  public static BeanA getBean(String flag) {
    if ("a".equals(flag)) {
      // Do something
    } else {
      // Do something
    }
    return new BeanA();
  }
}
