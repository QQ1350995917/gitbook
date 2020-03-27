package pwd.spring.boot.kill;

import java.lang.management.ManagementFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.GetMapping;
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
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@RestController
public class KillApplication implements DisposableBean {

  public static void main(String[] args) {
    String name = ManagementFactory.getRuntimeMXBean().getName();
    System.out.println(name);
    String pid = name.split("@")[0];
    System.out.println("Pid is:" + pid);

    SpringApplication.run(KillApplication.class, args);
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        System.out.println("执行 ShutdownHook ...");
      }
    }));
  }

  @Override
  public void destroy() throws Exception {
    System.out.println("destroy");
  }

  @GetMapping(value = "/test")
  public String test() {
    return "ok";
  }
}
