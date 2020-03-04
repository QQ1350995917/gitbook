package pwd.spring.mybatis.aop;

import org.springframework.stereotype.Component;

/**
 * pwd.spring.mybatis.aop@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-28 22:05
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Component
public class Bean {

  @Override
  public String toString() {
    System.out.println("hello " + Bean.class.getName());
    return super.toString();
  }
}
