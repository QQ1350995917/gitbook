package pwd.spring.boot.concurrent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
@EnableTransactionManagement
@MapperScan("pwd.spring.boot.concurrent.persistence.dao")
public class MybatisConcurrentApplication {


    public static void main(String[] args) {
        SpringApplication.run(MybatisConcurrentApplication.class, args);
    }

}
