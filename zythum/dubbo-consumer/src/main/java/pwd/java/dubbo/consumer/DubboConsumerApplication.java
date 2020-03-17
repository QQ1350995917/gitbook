package pwd.java.dubbo.consumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pwd.java.dubbo.api.GreetingsService;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-03-17 17:34
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class DubboConsumerApplication {

  public static void main( String[] args ) throws IOException {

    ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("consumer.xml");
    context.start();
    GreetingsService providerService = (GreetingsService) context.getBean("providerService");
    String str = providerService.sayHi("hello");
    System.out.println(str);
    System.in.read();

  }

}
