package pwd.spring.boot.concurrent.persistence.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pwd.spring.boot.concurrent.persistence.entity.TestEntity;

/**
 * pwd.spring.boot.concurrent.persistence.dao@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-12-10 17:01
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Mapper
public interface TestMapper {

    TestEntity queryByDay(@Param("day") Integer day);

    Integer updateById(@Param("id") Integer id, @Param("day") Integer day);
}
