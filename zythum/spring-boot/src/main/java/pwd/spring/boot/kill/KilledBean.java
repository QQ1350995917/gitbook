package pwd.spring.boot.kill;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

/**
 * pwd.spring.boot.kill@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-03-27 17:25
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Component
public class KilledBean implements DisposableBean {
  @Override
  public void destroy() throws Exception {
    System.out.println("测试 Bean 已销毁 ...");
  }

}
