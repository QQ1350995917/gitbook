package pwd.spring.boot.start;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;

/**
 * pwd.spring.boot.start@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-07-13 11:21
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@SpringBootApplication
@Slf4j
public class StartApplication implements ApplicationRunner, CommandLineRunner {

    @Autowired
    private StartService startService;

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("ApplicationRunner");
        startService.test();
    }
    @Override
    public void run(String... args) throws Exception {
        log.info("CommandLineRunner");
        startService.test();;
    }
}
