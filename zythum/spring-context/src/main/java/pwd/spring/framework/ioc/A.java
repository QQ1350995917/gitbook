package pwd.spring.mybatis.ioc;

/**
 * pwd.spring.mybatis.ioc@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-27 19:06
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public abstract class A {

  public void refreshBeanB() {
    BeanB beanB = getBeanB();
    // newest bean instance
  }

  public abstract BeanB getBeanB();
}
