package pwd.spring.mybatis.business;

import pwd.spring.mybatis.persistent.entity.Entity;

/**
 * pwd.spring.mybatis.business@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-11-28 23:54
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public interface EntityService {

  int add(Entity user);

  Entity findById(Integer id);
}
