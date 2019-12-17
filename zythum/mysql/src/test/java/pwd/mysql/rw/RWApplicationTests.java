package pwd.mysql.rw;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * pwd.mysql.rw@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-17 19:02
 *
 * @author DingPengwei[dingpengwei@eversec.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public class RWApplicationTests {
  @Autowired
  private MemberServiceImpl memberService;

  @Test
  public void testWrite() {
    Member member = new Member();
    member.setName("zhangsan");
    memberService.insert(member);
  }

  @Test
  public void testRead() {
    for (int i = 0; i < 4; i++) {
      memberService.selectAll();
    }
  }


  @Test
  public void testReadFromMaster() {
    memberService.getToken("1234");
  }

}
