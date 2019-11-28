## AOP和OOP的对比

OOP是自上而下（自左而右）的单维度代码逻辑，在随着工程扩大，业务复杂度变高，演进的过程中会出现如日志，权限，效率检测，事务管理等横切（纵切）问题。在OOP的概念中这些横切（纵切）代码会分散在各个业务侧，造成这些逻辑难以维护。而AOP编程思想就是把这些横切（纵切）逻辑和关键业务进行分离，从而达到主业务逻辑和辅助性业务逻辑解耦，实现高内聚，低耦合的设计思想。提高了开发效率和代码的复用性。

## AOP的底层实现

JDK动态代理（代理类有接口默认采用动态代理）

CGLIB代理（代理类没有接口默认采用CGLIB代理）

但是不是 绝对的，可以使用@EnableAspectJAutoProxy（proxyTargetClass = true\)强制采用CGLIB。

问题

编译期织入还是运行期织入？运行时

sping初始化织入还是getBean织入？初始化，在IOC容器中的对象初始化的时候就执行了织入。IOC容器名字singletonObjects，其本质是要给线程安全的HashMap。（具体织入是在singletonFactory.getObject\(\)时候织入）

springIOC从容器中拿到一个对象后，进行各种判断，判断其是否要使用代理（AbstractAutowireCapableBeanFactory查找exposedObject = this.initializeBean），是在initializeBean方法中完成代理。里面具体过程如下

```
AbstractAutoProxyCreator.java -> AopProxy
其中AopProxy有两个实现
CglibAopProxy
JdkDynamicAopProxy
那么如何识别用哪个实现呢，在DefaultAopProxyFactory中的createAopProxy中进行了判断，进行了好几个判断条件。
其中config.isProxyTargetClass默认为false，此时直接使用JDK动态代理。
但是config.isProxyTargetClass是可以配置的，在@Configuration标注的文件上开启@EnableAspectJAutoProxy（proxyTargetClass = true)
```

**跟中容器对象初始化的方法：大胆假设，小心求证，使用条件断点法。**

## SpringAOP和AspectJ的关系

AOP是编程目标

SpringAOP是AOP的实现方式。

AspectJ是AOP的实现方式。

SpringAOP在2.5中的编程风格不是很好，之后的版本借鉴了AspectJ的编程风格实现AOP。

官方文档中说明SpringAOP提供了两种编程风格

AspectJSupport，使用AspectJ注解

Schema-basedAOPSupport，使用xml配置

## SpringAOP中的概念（spring中的aop作用为方法级别）

Aspect（切面）：PointCut，JoinPoint，Advice定义的地方（文件）就是切面，切点一定要交予Spring管理（@Component）

PointCut（切点）：连接点的集合 （相对于连接点，切点可看作链接点的集合表）

```
public void save(){
    // before logger.log();

    save

    // after logger.log();
}

public void modify(){
    // before logger.log();

    modify

    // after logger.log();
}

public void delete(){
    // before logger.log();

    delete

    // after logger.log();
}

以上各个类，方法中的所有logger.log()加起来称之为切点
```

JoinPoint（连接点）：目标对象中的方法 （相对与切点，连接点可看作表中的一个记录），

```
public void save(){
    // before logger.log();

    save

    // after logger.log();
}
以上的looger.log()和save结合的地方就称之为连接点
```
Join Point 类型
1. execution：限定方法的方方面面，如访问修饰，返回值，参数等等。
2. within：限定的粒度为类,再次往下限定，则运行报错。
3. args：限定参数类型和数量，和包名类名无关，只要能够对上参数，则包名什么的无所谓是匹配
4. this：从容器中取出的对象是限定的对象实例（代理类和限定匹配）（可以在CGLIB中起作用）
4. target：从容器中取出的对象代理的目标对象是限定的对象实例（代理对象和限定匹配）
5. annotation：限定到方法上的注解
注意，以上类型的限定参数都支持@ 都支持逻辑运算符

参数：JoinPoint对象，ProceedingJoinPoint增强的JoinPoint可以增强环绕，可以动态替换参数，调用方法等。

Weaving（织入）：把代理逻辑加入到目标对象上的过程叫织入

Advice

```
public void save() throwing Exception { // after throwing advice
    // before logger.log(); // before(前置通知)
    try{
        save
    } catch(){
        // after throwing advice
    } finally{
        // after(final) advice
    }
    // after logger.log(); // after returen advice(后置通知)
}
```

around advice

Target

```
public class Controller {  // 目标对象
    public void save(){ // 连接点
        logger.log(); // 代理对象，代理逻辑
        save
    }
}
```

## Spring中开启AspectJ支持

[https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html\#aop-ataspectj](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#aop-ataspectj)

```
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

}

或者
<aop:aspectj-autoproxy/>
```

声明一个切面

[https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html\#aop-at-aspectj](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#aop-at-aspectj)

```
<bean id="myAspect" class="org.xyz.NotVeryUsefulAspect">
    <!-- configure properties of the aspect here -->
</bean>
或者
package org.xyz;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class NotVeryUsefulAspect {

}
```

声明一个切点

[https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html\#aop-pointcuts](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#aop-pointcuts)

```
@Pointcut("execution(* transfer(..))")// the pointcut expression // 切点是一个表，包含了springEL表达式，每个表达式是一个连接点
private void anyOldTransfer() {}// the pointcut signature
```

常用切点表达式

[https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html\#aop-pointcuts-examples](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#aop-pointcuts-examples)

声明一个通知

[https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html\#aop-advice](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#aop-advice)

```
package com.xyz.someapp;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SystemArchitecture {
    // 如果有如下切点
    @Pointcut("execution(* com.xyz.someapp.dao.*.*(..))")
    public void dataAccessOperation() {}

    // 则可以搭配如下通知，通知中的表达式是切点方法的全路径限定
    @Before("com.xyz.someapp.SystemArchitecture.dataAccessOperation()")
    public void doAccessCheckBefore() {
        // ...
    }

    // 则可以搭配如下通知，通知中的表达式是切点方法的全路径限定
    @After("com.xyz.someapp.SystemArchitecture.dataAccessOperation()")
    public void doAccessCheckAfter() {
        // ...
    }
}
```



