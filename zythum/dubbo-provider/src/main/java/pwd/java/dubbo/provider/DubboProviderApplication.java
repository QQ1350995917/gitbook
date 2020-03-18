package pwd.java.dubbo.provider;

import java.util.concurrent.CountDownLatch;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
public class DubboProviderApplication {


  public static void main(String[] args) throws Exception {
    System.setProperty("java.net.preferIPv4Stack", "true");
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
        "META-INF/spring/provider.xml");
    context.start();
    System.out.println("dubbo service started");
    new CountDownLatch(1).await();
  }

}
