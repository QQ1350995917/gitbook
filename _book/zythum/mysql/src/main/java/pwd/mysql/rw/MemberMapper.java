package pwd.mysql.rw;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * pwd.mysql.rw@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-17 19:10
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Mapper
public interface MemberMapper {
  void insert(Member member);
  List<Member> selectAll();
}
