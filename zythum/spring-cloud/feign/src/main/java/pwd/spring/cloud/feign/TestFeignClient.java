package pwd.spring.cloud.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * pwd.spring.cloud.feign@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-04-03 22:17
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@FeignClient(name = "storage", url = "https://github.com", configuration = FeignLogConfiguration.class)
public interface TestFeignClient {

  @RequestMapping(value = "", method = RequestMethod.GET)
  String index();
}
