package pwd.spring.boot.concurrent.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pwd.spring.boot.concurrent.business.TestService;
import pwd.spring.boot.concurrent.persistence.entity.TestEntity;

/**
 * pwd.initializr.account.test.business@ms-web-initializr
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-08-09 20:47
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BusinessTest {

    @Autowired
    private TestService testService;

    @Test
    public void testInsert() throws Exception {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                TestEntity testEntity = testService.queryByDay(1, 2);
                if (testEntity == null) {
                    log.error(Thread.currentThread().getName());
                } else {
                    log.error(testEntity.toString());
                }
            }, "thread-" + i).start();
        }

        Thread.sleep(Integer.MAX_VALUE);
    }
}
