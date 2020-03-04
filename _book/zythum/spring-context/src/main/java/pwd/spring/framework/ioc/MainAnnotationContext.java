package pwd.spring.mybatis.ioc;

import org.aspectj.lang.annotation.AdviceName;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * pwd.spring.mybatis.ioc@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-28 21:40
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Configuration
@ComponentScan("pwd.spring.mybatis.ioc")
public class MainAnnotationContext {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MainAnnotationContext.class);
    AnnotationBean bean = annotationConfigApplicationContext.getBean(AnnotationBean.class);
    bean.toString();
  }
}
