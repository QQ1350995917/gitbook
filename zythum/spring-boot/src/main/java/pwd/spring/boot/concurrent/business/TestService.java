package pwd.spring.boot.concurrent.business;

import org.apache.ibatis.annotations.Param;
import pwd.spring.boot.concurrent.persistence.entity.TestEntity;

/**
 * pwd.spring.boot.concurrent.business@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-12-10 17:09
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public interface TestService {

    TestEntity queryByDay(Integer today,Integer otherDay);
}
