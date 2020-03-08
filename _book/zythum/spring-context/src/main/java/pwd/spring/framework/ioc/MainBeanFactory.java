package pwd.spring.mybatis.ioc;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * pwd.spring.mybatis.ioc@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-27 20:32
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class MainBeanFactory {
  public static void main(String[] args) throws Throwable{
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource res = resolver.getResource("classpath:applicationContext.xml");
    System.out.println(res.getURL());
    BeanFactory beanFactory = new XmlBeanFactory(res);
    BeanA bean = beanFactory.getBean(BeanA.class);
  }
}
