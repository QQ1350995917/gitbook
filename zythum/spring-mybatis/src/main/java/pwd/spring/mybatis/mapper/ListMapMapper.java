package pwd.spring.mybatis.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * pwd.spring.mybatis.mapper@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-05 9:20
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public interface ListMapMapper {

  void insert(@Param(value = "domains") List<Map<String,String>> domains);

  void insertEnterprise(@Param(value = "enterprises") List<Map<String,String>> enterprises);

}
