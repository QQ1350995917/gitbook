package pwd.spring.cloud.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * pwd.spring.cloud@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-20 17:15
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@RestController
@RequestMapping(path = "")
public class EurekaClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(EurekaClientApplication.class, args);
  }


  @GetMapping(value = "/test")
  public String test() {
    return "ok";
  }
}
