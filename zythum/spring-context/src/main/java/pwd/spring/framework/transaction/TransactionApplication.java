package pwd.spring.framework.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * pwd.spring.framework.transaction@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-31 15:33
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@SpringBootApplication
@EnableTransactionManagement
public class TransactionApplication {

  public static void main(String[] args) {
    SpringApplication.run(TransactionApplication.class, args);
  }

  @Transactional(propagation = Propagation.REQUIRED,readOnly = false)
  public void test(){

  }
}
