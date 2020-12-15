package pwd.spring.boot.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * pwd.spring.boot.test@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-12-15 16:56
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@SpringBootApplication(scanBasePackages = "pwd.spring.boot.test")
public class TestApplication implements ApplicationListener {

    @Autowired
    private CustomerBean customerBean;

    @Autowired
    private CustomerQueue waitingForSendEmailQueue;
//    @Autowired
//    private CustomerThreadPoolExecutor emailPostManThreadPool;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        System.out.println();
    }


}
