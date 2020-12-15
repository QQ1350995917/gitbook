package pwd.spring.boot.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

/**
 * pwd.spring.boot.test@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-12-15 16:57
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Configuration
public class CustomerConfig {

    @Bean(name = "emailPostManThreadPoolQueue")
    public CustomerQueue<Runnable> getEmailPostManThreadPool() {
        return new CustomerQueue<>(1);
    }

    @Autowired
    @Bean(name = "emailPostManThreadPool")
    public ThreadPoolExecutor getEmailPostManThreadPool(
        @Qualifier(value = "emailPostManThreadPoolQueue") ArrayBlockingQueue<Runnable> queue) {
        return new ThreadPoolExecutor(1,
            1, 1, TimeUnit.MILLISECONDS,
            queue, (runnable) -> {
            return new Thread(runnable, "email-postman");
        });
    }

    @Bean
    public CustomerBean getCustomerBean(){
        return new CustomerBean();
    }


}
