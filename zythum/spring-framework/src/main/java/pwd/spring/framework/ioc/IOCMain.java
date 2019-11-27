package pwd.spring.framework.ioc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * pwd.spring.framework.ioc@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-27 11:17
 *
 * @author DingPengwei[dingpengwei@eversec.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class IOCMain {

  public static void main(String[] args) {
    ApplicationContext fileSystemXmlApplicationContext = new FileSystemXmlApplicationContext(
        "classpath:applicationContext.xml");
    BeanA bean = fileSystemXmlApplicationContext.getBean(BeanA.class);
  }
}
