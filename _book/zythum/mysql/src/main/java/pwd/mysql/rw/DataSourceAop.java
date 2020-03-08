package pwd.mysql.rw;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * pwd.mysql.rw@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-17 18:55
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Aspect
@Component
public class DataSourceAop {
  @Pointcut("!@annotation(pwd.mysql.rw.Master) " +
      "&& (execution(* pwd.mysql.rw..*.select*(..)) " +
      "|| execution(* pwd.mysql.rw..*.get*(..)))")
  public void readPointcut() {

  }

  @Pointcut("@annotation(pwd.mysql.rw.Master) " +
      "|| execution(* pwd.mysql.rw..*.insert*(..)) " +
      "|| execution(* pwd.mysql.rw..*.add*(..)) " +
      "|| execution(* pwd.mysql.rw..*.update*(..)) " +
      "|| execution(* pwd.mysql.rw..*.edit*(..)) " +
      "|| execution(* pwd.mysql.rw..*.delete*(..)) " +
      "|| execution(* pwd.mysql.rw..*.remove*(..))")
  public void writePointcut() {

  }

  @Before("readPointcut()")
  public void read() {
    DBContextHolder.slave();
  }

  @Before("writePointcut()")
  public void write() {
    DBContextHolder.master();
  }
}
