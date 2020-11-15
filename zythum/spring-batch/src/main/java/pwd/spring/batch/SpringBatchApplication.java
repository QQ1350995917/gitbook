package pwd.spring.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * pwd.spring.batch@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 * <a href="https://www.cnblogs.com/liululee/p/11124383.html">https://www.cnblogs.com/liululee/p/11124383.html</a>
 * <a href="https://segmentfault.com/a/1190000016278038">https://segmentfault.com/a/1190000016278038</a>
 *
 * date 2020-11-15 15:19
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SpringBatchApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringBatchApplication.class, args);
  }
}
