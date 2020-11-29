package pwd.spring.mybatis.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pwd.spring.mybatis.persistent.entity.Entity;
import pwd.spring.mybatis.persistent.mapper.EntityMapper;

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
@Service
public class EntityServiceImpl implements EntityService {

  @Autowired
  private EntityMapper mapper;

  @Override
  public int add(Entity user) {
    return 0;
  }

  @Override
  public Entity findById(Integer id) {
    return null;
  }
}
