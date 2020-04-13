package pwd.spring.cloud.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * pwd.spring.cloud@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-20 15:51
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@RestController
@EnableFeignClients
@SpringBootApplication
public class FeignApplication {
  @Autowired
  private TestFeignClient testFeignClient;

  public static void main(String[] args) {
    SpringApplication.run(FeignApplication.class, args);
  }

  @GetMapping("")
  public String index(){
    return testFeignClient.index();
  }
}
