package pwd.java.dubbo.provider;

import pwd.java.dubbo.api.GreetingsService;

/**
 * pwd.java.dubbo.provider@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-03-17 22:41
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class GreetingsServiceImpl implements GreetingsService {
  @Override
  public String sayHi(String name) {
    return "hi, pwd,hi " + name;
  }
}
