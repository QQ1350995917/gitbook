package pwd.spring.framework.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * pwd.spring.framework.aop@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-28 22:04
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Configuration
@ComponentScan("pwd.spring.framework.aop")
@EnableAspectJAutoProxy
public class AopApplication {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(
        AopApplication.class);
    Bean bean = annotationConfigApplicationContext.getBean(Bean.class);
    bean.toString();

  }
}
