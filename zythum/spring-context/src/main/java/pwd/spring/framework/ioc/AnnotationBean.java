package pwd.spring.mybatis.ioc;

import org.springframework.stereotype.Component;

/**
 * pwd.spring.mybatis.ioc@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-28 21:41
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Component
public class AnnotationBean {

  @Override
  public String toString() {
    System.out.println("hello Annotation Application Context");
    return super.toString();
  }
}
