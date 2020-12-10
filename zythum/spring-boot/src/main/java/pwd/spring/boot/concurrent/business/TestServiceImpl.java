package pwd.spring.boot.concurrent.business;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import pwd.spring.boot.concurrent.persistence.dao.TestMapper;
import pwd.spring.boot.concurrent.persistence.entity.TestEntity;

/**
 * pwd.spring.boot.concurrent.business@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-12-10 17:10
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Service
public class TestServiceImpl implements TestService {

    @Resource
    private TestMapper testMapper;

//    @Resource
//    PlatformTransactionManager platformTransactionManager;
//    @Resource
//    TransactionDefinition transactionDefinition;


    @Transactional
    @Override
    public TestEntity queryByDay(Integer today, Integer otherDay) {
//        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        TestEntity testEntity = testMapper.queryByDay(today);
        if (testEntity != null) {
            testMapper.updateById(testEntity.getId(), otherDay);
        }

//        platformTransactionManager.commit(transactionStatus);

        return testEntity;
    }
}
