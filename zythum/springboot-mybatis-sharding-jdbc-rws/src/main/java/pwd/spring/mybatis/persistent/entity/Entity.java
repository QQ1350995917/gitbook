package pwd.spring.mybatis.persistent.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * pwd.spring.mybatis.persistent.entity@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-11-28 23:50
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Entity {

  private Integer id;

  private String username;

  private String password;

  private Date birthday;
}
