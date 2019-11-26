package pwd.spring.cloud.eureka;

import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * pwd.spring.cloud.eureka@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-25 10:35
 *
 * @author DingPengwei[dingpengwei@eversec.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Aspect
@Component
public class EurekaClientHttpWatcher {


  @Pointcut("execution(public * org.apache.http.client.RequestDirector.execute(..))")
  public void request() {

  }

  @Around("pwd.spring.cloud.eureka.EurekaClientHttpWatcher.request()")
  public Object watchRequestAndResponse(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    System.out.println("watchRequestAndResponse start.....");
    try {
      System.out.println("ARGS : " + Arrays.toString(proceedingJoinPoint.getArgs()));
      Object o = proceedingJoinPoint.proceed();
      System.out.println("watchRequestAndResponse :" + o);
      return o;
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  @Pointcut("execution(public * pwd.spring.cloud.eureka.EurekaClientApplication.test(..))")
  public void test() {

  }

  @Around("pwd.spring.cloud.eureka.EurekaClientHttpWatcher.test()")
  public Object test(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    Object o = proceedingJoinPoint.proceed();
    return o;
  }

}
