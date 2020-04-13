package pwd.spring.cloud.feign;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * pwd.spring.cloud.feign@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-04-03 22:16
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Configuration
public class FeignLogConfiguration {
  @Bean
  Logger.Level feignLoggerLevel(){
    return Logger.Level.FULL;
  }
}
