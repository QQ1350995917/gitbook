package pwd.spring.cloud.eureka;

import java.util.LinkedHashMap;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * pwd.spring.cloud@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-11-20 17:15
 *
 * @author DingPengwei[dingpengwei@eversec.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@EnableDiscoveryClient
@SpringBootApplication
public class EurekaClientApplication {
  public static void main(String[] args) {
    SpringApplication.run(EurekaClientApplication.class, args);
  }


//  @Aspect
//  @Component
//  public class PersonAspect {
//
//    @Pointcut(value = "execution(public String doExecute(..))")
//    public void pointCut(){
//
//    }
//
//    @Around("pointCut()")
//    public Object beforeAddActivitiExecuteHandler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//      Map<String, Object> nameAndArgs = getFieldsName(proceedingJoinPoint);
//      return proceedingJoinPoint.proceed(nameAndArgs.values().toArray());
//    }
//
//
//    private Map<String, Object> getFieldsName(ProceedingJoinPoint point) {
//      Map<String, Object> map = new LinkedHashMap<>();
//      MethodSignature methodSignature = (MethodSignature) point.getSignature();
//      String[] parameterNames = methodSignature.getParameterNames();
//      Class[] parameterTypes = methodSignature.getParameterTypes();
//      Object[] args = point.getArgs();
//      for (int i = 0; i < args.length; i++) {
//        map.put(parameterNames[i], args[i]);
//      }
//      return map;
//    }
//  }
}
