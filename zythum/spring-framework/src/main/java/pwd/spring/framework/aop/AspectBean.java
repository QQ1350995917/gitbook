package pwd.spring.framework.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * pwd.spring.framework.aop@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-28 22:05
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Component
@Aspect
public class AspectBean {
  @Pointcut("execution(* pwd.spring.framework.aop.Bean.*(..))")
  public void pointCut(){}

  @Before("pwd.spring.framework.aop.AspectBean.pointCut()")
  public void beforePointCut(){
    System.out.println("before");
  }

  @After("pwd.spring.framework.aop.AspectBean.pointCut()")
  public void afterPointCut(){
    System.out.println("after");
  }

  @Around("pwd.spring.framework.aop.AspectBean.pointCut()")
  public Object aroundPointCut(ProceedingJoinPoint pjp){
    Object result = null;
    System.out.println("Around - before");
    try {
      Object[] args = pjp.getArgs();
      result = pjp.proceed(args);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    System.out.println("Around - after");
    return result;
  }

  @AfterThrowing("pwd.spring.framework.aop.AspectBean.pointCut()")
  public void afterThrowingPointCut(){
    System.out.println("AfterThrowing");
  }

  @AfterReturning("pwd.spring.framework.aop.AspectBean.pointCut()")
  public void afterReturnPointCut(){
    System.out.println("AfterReturning");
  }
}
