package pwd.mysql.rw;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * pwd.mysql.rw@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-17 18:58
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Service
public class MemberServiceImpl {
  @Autowired
  private MemberMapper memberMapper;

  @Transactional
  public void insert(Member member) {
    memberMapper.insert(member);
  }

  public List<Member> selectAll() {
    return memberMapper.selectAll();
  }

  @Master
  public String getToken(String appId) {
    //  有些读操作必须读主数据库
    //  比如，获取微信access_token，因为高峰时期主从同步可能延迟
    //  这种情况下就必须强制从主数据读
    return null;
  }
}
