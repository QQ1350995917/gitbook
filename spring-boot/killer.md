# Kill Application

## [linux系统中的kill](../linux/kill.md)

## 1 增加一个实现了 DisposableBean 接口的类
```java
@Component
public class TestDisposableBean implements DisposableBean{
   @Override
    public void destroy() throws Exception {
      System.out.println("测试 Bean 已销毁 ...");
    }
}
```
```java
@SpringBootApplication
@RestController
public class TestShutdownApplication implements DisposableBean {
  public static void main( String [] args) {
    SpringApplication.run( TestShutdownApplication.class, args);
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        @Override
        public void run() {
          System.out.println("执行 ShutdownHook ...");
        }
      }));
    }
}
```
- 测试步骤

1. 执行 java-jar test-shutdown-1.0.jar 将应用运行起来
 
2. 测试 kill-9pid， kill-15pid， ctrl+c 后输出日志内容

- 测试结果(kill-15 pid & ctrl+c，效果一样，输出结果如下)
```java
2018-01-14 16:55:32.424  INFO 8762 --- [       Thread-3] ationConfigEmbeddedWebApplicationContext : Closing org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext@2cdf8d8a: startup date [Sun Jan 14 16:55:24 UTC 2018]; root of context hierarchy

2018-01-14 16:55:32.432  INFO 8762 --- [       Thread-3] o.s.j.e.a.AnnotationMBeanExporter        : Unregistering JMX-exposed beans on shutdown

执行 ShutdownHook ...

测试 Bean 已销毁 ...

java -jar test-shutdown-1.0.jar  7.46s user 0.30s system 80% cpu 9.674 total
```

- kill-9 pid，没有输出任何应用日志

## SpringBoot kill -15 pid
先是由 AnnotationConfigEmbeddedWebApplicationContext （一个 ApplicationContext 的实现类）收到了通知，紧接着执行了测试代码中的 Shutdown Hook，最后执行了 DisposableBean#destory() 方法。

一般我们会在应用关闭时处理一下“善后”的逻辑，比如

1. 关闭 socket 链接
1. 清理临时文件
1. 发送消息通知给订阅方，告知自己下线
1. 将自己将要被销毁的消息通知给子进程
1. 各种资源的释放
1. ... ...

在Spring容器初始化时（AbstractApplicationContext#registerShutdownHook），ApplicationContext 便已经注册了一个 Shutdown Hook，这个钩子调用了 Close() 方法，于是当我们执行 kill -15 pid 时，JVM 接收到关闭指令，触发了这个 Shutdown Hook，进而由 Close() 方法去处理一些善后手段。具体的善后手段有哪些，则完全依赖于 ApplicationContext 的 doClose() 逻辑，包括了注释中提及的销毁缓存单例对象，发布 close 事件，关闭应用上下文等等，特别的，当 ApplicationContext 的实现类是 AnnotationConfigEmbeddedWebApplicationContext 时，还会处理一些 tomcat/jetty 一类内置应用服务器关闭的逻辑。

窥见了 springboot 内部的这些细节，更加应该了解到优雅关闭应用的必要性。JAVA 和 C 都提供了对 Signal 的封装，我们也可以手动捕获操作系统的这些 Signal。


## spring-boot-starter-actuator 模块提供了一个 restful 接口，用于优雅停机。
添加依赖
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

添加配置
```properties
#启用shutdown
endpoints.shutdown.enabled=true
#禁用密码验证
endpoints.shutdown.sensitive=false
```
生产中请注意该端口需要设置权限，如配合 spring-security 使用。

执行 curl-X POST host:port/shutdown 指令，关闭成功便可以获得如下的返回：
```json
{"message":"Shutting down, bye..."}
```
该方法和kill -15 pid 的方式达到的效果相同

## 如何销毁作为成员变量的线程池 
尽管 JVM 关闭时会帮我们回收一定的资源，但一些服务如果大量使用异步回调，定时任务，处理不当很有可能会导致业务出现问题，在这其中，线程池如何关闭是一个比较典型的问题。

我们需要想办法在应用关闭时（JVM 关闭，容器停止运行），关闭线程池。

初始方案：什么都不做。在一般情况下，这不会有什么大问题，因为 JVM 关闭，会释放之，但显然没有做到本文一直在强调的两个字，没错----优雅。

方法一的弊端在于线程池中提交的任务以及阻塞队列中未执行的任务变得极其不可控，接收到停机指令后是立刻退出？还是等待任务执行完成？抑或是等待一定时间任务还没执行完成则关闭？

方案改进：

发现初始方案的劣势后，我立刻想到了使用 DisposableBean 接口，像这样：

```java
@Service
public class SomeService implements DisposableBean{
   ExecutorService executorService = Executors.newFixedThreadPool(10);
   public void concurrentExecute() {
       executorService.execute(new Runnable() {
           @Override
           public void run() {
               System.out.println("executed...");
           }
       });
   }

   @Override
   public void destroy() throws Exception {
       executorService.shutdownNow();
       //executorService.shutdown();
   }
}
```
紧接着问题又来了，是 shutdown 还是 shutdownNow 呢？这两个方法还是经常被误用的，简单对比这两个方法。

ThreadPoolExecutor 在 shutdown 之后会变成 SHUTDOWN 状态，无法接受新的任务，随后等待正在执行的任务执行完成。意味着，shutdown 只是发出一个命令，至于有没有关闭还是得看线程自己。

ThreadPoolExecutor 对于 shutdownNow 的处理则不太一样，方法执行之后变成 STOP 状态，并对执行中的线程调用 Thread.interrupt() 方法（但如果线程未处理中断，则不会有任何事发生），所以并不代表“立刻关闭”。

查看 shutdown 和 shutdownNow 的 java doc，会发现如下的提示：

> shutdown() ：Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted.Invocation has no additional effect if already shut down.This method does not wait for previously submitted tasks to complete execution.Use {@link #awaitTermination awaitTermination} to do that.
shutdownNow()：Attempts to stop all actively executing tasks, halts the processing of waiting tasks, and returns a list of the tasks that were awaiting execution. These tasks are drained (removed) from the task queue upon return from this method.This method does not wait for actively executing tasks to terminate. Use {@link #awaitTermination awaitTermination} to do that.There are no guarantees beyond best-effort attempts to stop processing actively executing tasks. This implementation cancels tasks via {@link Thread#interrupt}, so any task that fails to respond to interrupts may never terminate.

两者都提示我们需要额外执行 awaitTermination 方法，仅仅执行 shutdown/shutdownNow 是不够的。

最终方案：参考 spring 中线程池的回收策略，我们得到了最终的解决方案。

```java
public abstract class ExecutorConfigurationSupport extends CustomizableThreadFactory implements DisposableBean{
   @Override
   public void destroy() {
       shutdown();
   }
   
   public void shutdown() {
       if (this.waitForTasksToCompleteOnShutdown) {
           this.executor.shutdown();
       } else {
           this.executor.shutdownNow();
       }
       awaitTerminationIfNecessary();
   }

   private void awaitTerminationIfNecessary() {
       if (this.awaitTerminationSeconds > 0) {
           try {
               this.executor.awaitTermination(this.awaitTerminationSeconds, TimeUnit.SECONDS));
           } catch (InterruptedException ex) {
               Thread.currentThread().interrupt();
           }
       }
   }
}
```  
保留了注释，去除了一些日志代码，一个优雅关闭线程池的方案呈现在我们的眼前。

1. 通过 waitForTasksToCompleteOnShutdown 标志来控制是想立刻终止所有任务，还是等待任务执行完成后退出。
2. executor.awaitTermination(this.awaitTerminationSeconds, TimeUnit.SECONDS)); 控制等待的时间，防止任务无限期的运行（前面已经强调过了，即使是 shutdownNow 也不能保证线程一定停止运行）。

## 更多需要思考的优雅停机策略
在我们分析 RPC 原理的系列文章里面曾经提到，服务治理框架一般会考虑到优雅停机的问题。通常的做法是事先隔断流量，接着关闭应用。常见的做法是将服务节点从注册中心摘除，订阅者接收通知，移除节点，从而优雅停机；涉及到数据库操作，则可以使用事务的 ACID 特性来保证即使 crash 停机也能保证不出现异常数据，正常下线则更不用说了；又比如消息队列可以依靠 ACK 机制+消息持久化，或者是事务消息保障；定时任务较多的服务，处理下线则特别需要注意优雅停机的问题，因为这是一个长时间运行的服务，比其他情况更容易受停机问题的影响，可以使用幂等和标志位的方式来设计定时任务...

事务和 ACK 这类特性的支持，即使是宕机，停电，kill -9 pid 等情况，也可以使服务尽量可靠；而同样需要我们思考的还有 kill -15 pid，正常下线等情况下的停机策略。最后再补充下整理这个问题时，自己对 jvm shutdown hook 的一些理解。

> When the virtual machine begins its shutdown sequence it will start all registered shutdown hooks in some unspecified order and let them run concurrently. When all the hooks have finished it will then run all uninvoked finalizers if finalization-on-exit has been enabled. Finally, the virtual machine will halt.
   
    
shutdown hook 会保证 JVM 一直运行，直到 hook 终止 (terminated)。这也启示我们，如果接收到 kill -15 pid 命令时，执行阻塞操作，可以做到等待任务执行完成之后再关闭 JVM。同时，也解释了一些应用执行 kill -15 pid 无法退出的问题，没错，中断被阻塞了。



## 参考资料
https://mp.weixin.qq.com/s/RQaVlxA9uiP0G3GHACzPwQ
