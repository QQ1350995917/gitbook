package pwd.spring.framework.ioc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

/**
 * pwd.spring.framework.ioc@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-27 14:11
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Getter
@Setter
public class BeanAFactoryBean implements FactoryBean {

  private Long id;
  private String name;

  @Override
  public boolean isSingleton() {
    // false 每次创建都要调用getObject()
    return true;
  }

  @Override
  public Object getObject() throws Exception {
    return new BeanA();
  }

  @Override
  public Class<?> getObjectType() {
    return BeanA.class;
  }
}
