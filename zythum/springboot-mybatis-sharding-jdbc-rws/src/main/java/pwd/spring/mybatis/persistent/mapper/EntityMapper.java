package pwd.spring.mybatis.persistent.mapper;

import pwd.spring.mybatis.persistent.entity.Entity;

/**
 * pwd.spring.mybatis.persistent.mapper@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-11-28 23:51
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public interface EntityMapper {

  int deleteByPrimaryKey(Integer id);

  int insert(Entity record);

  int insertSelective(Entity record);

  Entity selectByPrimaryKey(Integer id);

  int updateByPrimaryKey(Entity record);

  int updateByPrimaryKeySelective(Entity record);
}
