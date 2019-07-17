## AOP和OOP的对比

OOP是自上而下（自左而右）的单维度代码逻辑，在随着工程扩大，业务复杂度变高，演进的过程中会出现如日志，权限，效率检测，事务管理等横切（纵切）问题。在OOP的概念中这些横切（纵切）代码会分散在各个业务侧，造成这些逻辑难以维护。而AOP编程思想就是把这些横切（纵切）逻辑和关键业务进行分离，从而达到主业务逻辑和辅助性业务逻辑解耦，实现高内聚，低耦合的设计思想。提高了开发效率和代码的复用性。

## AOP的底层实现

JDK动态代理

CGLIB代理

问题

编译期织入还是运行期织入？运行时

sping初始化织入还是getBean织入？

## SpringAOP和AspectJ的关系

AOP是编程目标

SpringAOP是AOP的实现方式。

AspectJ是AOP的实现方式。

SpringAOP在2.5中的编程风格不是很好，之后的版本借鉴了AspectJ的编程风格实现AOP。

官方文档中说明SpringAOP提供了两种编程风格

AspectJSupport，使用AspectJ注解

Schema-basedAOPSupport，使用xml配置

## SpringAOP中的概念（spring中的aop作用为方法级别）

Aspect（切面）：PointCut，JoinPoint，Advice定义的地方（文件）就是切面

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
    public void doAccessCheck() {
        // ...
    }
}
```







