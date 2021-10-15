# 事务与隔离

## SpringBoot使用事物的步骤：

### 第一步：在启动类上开启事物支持

```java
@SpringBootApplication
@EnableTransactionManagement
public class TransactionApplication {

}
```

@EnableTransactionManagement注解其实在大多数情况下，不是必须的，因为SpringBoot在TransactionAutoConfiguration类里为我们自动配置启用了@EnableTransactionManagement注解。不过自动启用该注解有两个前提条件，分别是：

* @ConditionalOnBean(PlatformTransactionManager.class)
* @ConditionalOnMissingBean(AbstractTransactionManagementConfiguration.class)

而一般情况下，这两个条件都是满足的，所以一般的，我们在启动类上写不写@EnableTransactionManagement都行。这里还是建议写出来。

### 第二步：在业务逻辑层接口的实现类中的相关方法上声明事物

```java
@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,readOnly = false)
public void test(){

}
```

Isolation.DEFAULT（-1）是Spring事务的默认隔离级别，该隔离级别的含义是spring依赖数据库的隔离级别。

### 最佳实践

* service实现类(一般不建议在接口上)上添加@Transactional，可以将整个类纳入spring事务管理，在每个业务方法执行时都会开启一个事务，不过这些事务采用相同的管理方式。
* Transactional 注解只能应用到 public 可见度的方法上。 如果应用在protected、private或者 package可见度的方法上，也不会报错，不过事务设置不会起作用。
* 默认情况下，Transactional 注解的事物所管理的方法中，如果方法抛出运行时异常或error，那么会进行事务回滚；如果方法抛出的是非运行时异常，那么不会回滚。
  * 注：SQL异常属于检查异常（有的框架将SQL异常重写为了运行时异常），但是有时我们写SQL时，检查异常并不会提示；而默认情况下，事物对检查异常不会作出回滚处理。
  * 注：在很多时候，我们除了catch一般的异常或自定义异常外，我们还习惯于catch住Exception异常；然后再抛出Exception异常。但是Exception异常属于非运行时异常(即：检查异常)，因为默认是运行时异常时事物才进行回滚，那么这种情况下，是不会回滚的。我们可以在@Transacional注解中，通过rollbackFor = {Exception.class} 来解决这个问题。即：设置当Exception异常或Exception的所有任意子类异常时事物会进行回滚。
  * 注：被catch处理了的异常，不会被事物作为判断依据；如果异常被catch 了，但是又在catch中抛出了新的异常，那么事物会以这个新的异常作 为是否进行回滚的判断依据。

### Transactional 注解的常用属性表

| 属性            | 说明                                                                        |
| ------------- | ------------------------------------------------------------------------- |
| propagation   | 事务的传播行为，默认值为 REQUIRED。                                                    |
| isolation     | 事务的隔离度，默认值采用 DEFAULT                                                      |
| timeout       | 事务的超时时间，默认值为-1，不超时。如果设置了超时时间(单位秒)，那么如果超过该时间限制了但事务还没有完成，则自动回滚事务。           |
| read-only     | 指定事务是否为只读事务，默认值为 false；为了忽略那些不需要事务的方法，比如读取数据，可以设置 read-only 为 true。       |
| rollbackFor   | 用于指定能够触发事务回滚的异常类型，如果有多个异常类型需要指定，各类型之间可以通过逗号分隔。{xxx1.class, xxx2.class,……} |
| noRollbackFor | 抛出 no-rollback-for 指定的异常类型，不回滚事务。{xxx1.class, xxx2.class,……}              |

## Spring事务失效的场景

### 数据库引擎不支持事务

这里以 MySQL 为例，其 MyISAM 引擎是不支持事务操作的，InnoDB 才是支持事务的引擎，一般要支持事务都会使用 InnoDB。从 MySQL 5.5.5 开始的默认存储引擎是：InnoDB，之前默认的都是：MyISAM，所以这点要值得注意，底层引擎不支持事务再怎么搞都是白搭。

### 没有被 Spring 管理

```java
// @Service
public class OrderServiceImpl implements OrderService {   
 @Transactional    
public void updateOrder(Order order) {       
 // update order；  
  }
}
```

如果此时把 @Service 注解注释掉，这个类就不会被加载成一个 Bean，那这个类就不会被 Spring 管理了，事务自然就失效了。

### 方法不是 public的

该异常一般情况都会被编译器帮忙识别

以下来自 Spring 官方文档：

> When using proxies, you should apply the @Transactional annotation only to methods with public visibility. If you do annotate protected, private or package-visible methods with the @Transactional annotation, no error is raised, but the annotated method does not exhibit the configured transactional settings. Consider the use of AspectJ (see below) if you need to annotate non-public methods.

大概意思就是 @Transactional 只能用于 public 的方法上，否则事务不会失效，如果要用在非 public 方法上，可以开启 AspectJ 代理模式。

```java
@Service
public class DemoServiceImpl implements  DemoService {

    @Transactional(rollbackFor = SQLException.class)
    @Override
     int saveAll(){  // 编译器一般都会在这个地方给出错误提示
        // do someThing;
        return  1;
    }
}
```

[如何开启AspectJ支持](aop-index.md)

### 自身调用问题

来看两个示例：

```java
@Service
public class OrderServiceImpl implements OrderService {
    public void update(Order order) {
        this.updateOrder(order);
    }
    @Transactional
    public void updateOrder(Order order) {
        // update order；
    }
}
```

update方法上面没有加 @Transactional 注解，调用有 @Transactional 注解的 updateOrder 方法，updateOrder 方法上的事务管用吗？

再来看下面这个例子：

```java
@Service
public class OrderServiceImpl implements OrderService {
    @Transactional
    public void update(Order order) {
        this.updateOrder(order); 
   }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Transactional(propagation = Propagation.PROPAGATION_REQUIRED)
    public void updateOrder(Order order) {
        // update order；
    }
}
```

这次在 update 方法上加了 @Transactional，updateOrder 加了 REQUIRES_NEW 或者PROPAGATION_REQUIRED新开启一个事务，那么新开的事务管用么？

这两个例子的答案是：不管用！

因为它们发生了自身调用，就调该类自己的方法，而没有经过 Spring 的代理类，默认只有在外部调用事务才会生效，这也是老生常谈的经典问题了。

问题原因： 不带事务的方法通过this调用该类中带事务的方法，不会回滚。因为spring的回滚是用过代理模式生成的，如果是一个不带事务的方法调用该类的带事务的方法，直接通过this.xxx()调用，而不生成代理事务，所以事务不起作用。常见解决方法，拆类。spring中在一个拥有事务的方法A中调用另一个会挂起事务并创建新事务的方法B，如果使用this调用这个方法B， 此时方法B抛出了一个一场，此时的方法B的事务会失效的。并不会回滚。JDK的动态代理。只有被动态代理直接调用时才会产生事务。在SpringIoC容器中返回的调用的对象是代理对象而不是真实的对象。而这里的this是EmployeeService真实对象而不是代理对象。

这个的解决方案之一就是在的类中注入自己，用注入的对象再调用另外一个方法。

也可以在方法update上不开启事务，方法updateOrder上开启事务，并在方法update中将this调用改成动态代理调用(AopContext.currentProxy()),如下：

```java
@Service
public class OrderServiceImpl implements OrderService {
    public void update(Order order) {
        OrderService proxy =(OrderService) AopContext.currentProxy();
        proxy.updateOrder(order);
   }

    @Transactional
    public void updateOrder(Order order) {
        // update order；
    }
}
```

这个不太优雅，另外一个可行的方案可以参考[Spring 如何在一个事务中开启另一个事务](https://mp.weixin.qq.com/s?\__biz=MzI3ODcxMzQzMw==\&mid=2247491775\&idx=2\&sn=142f1d6ab0415f17a413a852efbde54f\&scene=21#wechat_redirect)这篇文章。

### 数据源没有配置事务管理器

```java
@Bean
public PlatformTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
}
```

如上面所示，当前数据源若没有配置事务管理器，那也是白搭！

### 不支持事务

```java
@Service
public class OrderServiceImpl implements OrderService {
    @Transactional
    public void update(Order order) {
        updateOrder(order);
    }
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateOrder(Order order) {
        // update order；
    }
}
```

Propagation.NOT_SUPPORTED： 表示不以事务运行，当前若存在事务则挂起，详细的可以参考[事务隔离级别和传播机制](https://mp.weixin.qq.com/s?\__biz=MzI3ODcxMzQzMw==\&mid=2247483796\&idx=1\&sn=a11835fb6cdf4d957b5748ae916e53b7\&scene=21#wechat_redirect)这篇文章。都主动不支持以事务方式运行了，那事务生效也是白搭！

### 异常被吃了

这个也是出现比较多的场景：

```java
@Service
public class OrderServiceImpl implements OrderService {
    @Transactional
    public void updateOrder(Order order) {
        try {
            // update order;
         }catch (Exception e){
            //do something;
        }
    }
}
```

把异常吃了，然后又不抛出来，事务就不生效了

### 异常类型错误或格式配置错误

上面的例子再抛出一个异常：

```java
@Service
public class OrderServiceImpl implements OrderService {
 @Transactional
   // @Transactional(rollbackFor = SQLException.class)
    public void updateOrder(Order order) {
        try {            // update order
          }catch (Exception e){
           throw new Exception("更新错误");        
        }    
    }
}
```

这样事务也是不生效的，因为默认回滚的是：RuntimeException，如果你想触发其他异常的回滚，需要在注解上配置一下，如：

```java
@Transactional(rollbackFor = Exception.class)
```

java的设计中，它认为不继承RuntimeException的异常是”checkException”或普通异常，如IOException，这些异常在java语法中是要求强制处理的。对于这些普通异常，spring默认它们都已经处理，所以默认不回滚。可以添加rollbackfor=Exception.class来表示所有的Exception都回滚。这个配置仅限于 Throwable 异常类及其子类。

## 阿里规范推荐

事务场景中，抛出异常被catch后，如果需要回滚，一定要手动回滚事务。错误使用方式 ![](../../.gitbook/assets/transactional-ali-01.png) 正确使用方式 ![](../../.gitbook/assets/transactional-ali-02.png) 阿里推荐的方式属于自动提交/手动回滚，那如果我们想要手动提交、手动回滚的话，可参考（注：不需要@Transactional注解。）: ![](../../.gitbook/assets/transactional-ali-03.png)

## spring事务管理(详解和实例)

### [事务理解-以MySQL为例](https://github.com/QQ1350995917/gitbook/tree/1301eb5173842b2e1ed6ae91795462f32063b906/spring/storage/databases/mysql/chapter05.md)

### 核心接口

Spring事务管理的实现有许多细节，如果对整个接口框架有个大体了解会非常有利于我们理解事务，下面通过讲解Spring的事务接口来了解Spring实现事务的具体策略。 Spring事务管理涉及的接口的联系如下： ![](../../.gitbook/assets/transactional-01.png)

#### 事务管理器

Spring并不直接管理事务，而是提供了多种事务管理器，他们将事务管理的职责委托给Hibernate或者JTA等持久化机制所提供的相关平台框架的事务来实现。 Spring事务管理器的接口是org.springframework.transaction.PlatformTransactionManager，通过这个接口，Spring为各个平台如JDBC、Hibernate等都提供了对应的事务管理器，但是具体的实现就是各个平台自己的事情了。此接口的内容如下：

```java
Public interface PlatformTransactionManager(){  
    // 由TransactionDefinition得到TransactionStatus对象
    TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException; 
    // 提交
    Void commit(TransactionStatus status) throws TransactionException;  
    // 回滚
    Void rollback(TransactionStatus status) throws TransactionException;  
    }
```

从这里可知具体的具体的事务管理机制对Spring来说是透明的，它并不关心那些，那些是对应各个平台需要关心的，所以Spring事务管理的一个优点就是为不同的事务API提供一致的编程模型，如JTA、JDBC、Hibernate、JPA。下面分别介绍各个平台框架实现事务管理的机制。

#### JDBC事务

如果应用程序中直接使用JDBC来进行持久化，DataSourceTransactionManager会为你处理事务边界。为了使用DataSourceTransactionManager，你需要使用如下的XML将其装配到应用程序的上下文定义中：

```markup
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource" />
</bean>
```

实际上，DataSourceTransactionManager是通过调用java.sql.Connection来管理事务，而后者是通过DataSource获取到的。通过调用连接的commit()方法来提交事务，同样，事务失败则通过调用rollback()方法进行回滚。

#### Hibernate事务

如果应用程序的持久化是通过Hibernate实习的，那么你需要使用HibernateTransactionManager。对于Hibernate3，需要在Spring上下文定义中添加如下的声明：

```markup
<bean id="transactionManager" class="org.springframework.orm.hibernate3.HibernateTransactionManager">
    <property name="sessionFactory" ref="sessionFactory" />
</bean>
```

sessionFactory属性需要装配一个Hibernate的session工厂，HibernateTransactionManager的实现细节是它将事务管理的职责委托给org.hibernate.Transaction对象，而后者是从Hibernate Session中获取到的。当事务成功完成时，HibernateTransactionManager将会调用Transaction对象的commit()方法，反之，将会调用rollback()方法。

#### Java持久化API事务（JPA）

Hibernate多年来一直是事实上的Java持久化标准，但是现在Java持久化API作为真正的Java持久化标准进入大家的视野。如果你计划使用JPA的话，那你需要使用Spring的JpaTransactionManager来处理事务。你需要在Spring中这样配置JpaTransactionManager：

```
<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
    <property name="sessionFactory" ref="sessionFactory" />
</bean>
```

JpaTransactionManager只需要装配一个JPA实体管理工厂（javax.persistence.EntityManagerFactory接口的任意实现）。JpaTransactionManager将与由工厂所产生的JPA EntityManager合作来构建事务。

#### Java原生API事务

如果你没有使用以上所述的事务管理，或者是跨越了多个事务管理源（比如两个或者是多个不同的数据源），你就需要使用JtaTransactionManager:

```
<bean id="transactionManager" class="org.springframework.transaction.jta.JtaTransactionManager">
    <property name="transactionManagerName" value="java:/TransactionManager" />
</bean>
```

JtaTransactionManager将事务管理的责任委托给javax.transaction.UserTransaction和javax.transaction.TransactionManager对象，其中事务成功完成通过UserTransaction.commit()方法提交，事务失败通过UserTransaction.rollback()方法回滚。

### 基本事务属性的定义

上面讲到的事务管理器接口PlatformTransactionManager通过getTransaction(TransactionDefinition definition)方法来得到事务，这个方法里面的参数是TransactionDefinition类，这个类就定义了一些基本的事务属性。 那么什么是事务属性呢？事务属性可以理解成事务的一些基本配置，描述了事务策略如何应用到方法上。事务属性包含了5个方面，如图所示： ![](../../.gitbook/assets/transactional-02.png)

而TransactionDefinition接口内容如下：

```
public interface TransactionDefinition {
    int getPropagationBehavior(); // 返回事务的传播行为
    int getIsolationLevel(); // 返回事务的隔离级别，事务管理器根据它来控制另外一个事务可以看到本事务内的哪些数据
    int getTimeout();  // 返回事务必须在多少秒内完成
    boolean isReadOnly(); // 事务是否只读，事务管理器能够根据这个返回值进行优化，确保事务是只读的
}
```

我们可以发现TransactionDefinition正好用来定义事务属性，下面详细介绍一下各个事务属性。

## spring事务传播行为

事务的第一个方面是传播行为（propagation behavior）。当事务方法被另一个事务方法调用时，必须指定事务应该如何传播。例如：方法可能继续在现有事务中运行，也可能开启一个新事务，并在自己的事务中运行。Spring定义了七种传播行为：

| 传播行为                      | 含义                                                                                                                                             |
| ------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| PROPAGATION_NEVER         | 表示当前方法不应该运行在事务上下文中。如果当前正有一个事务在运行，则会抛出异常                                                                                                        |
| PROPAGATION_NOT_SUPPORTED | 表示该方法不应该运行在事务中。如果存在当前事务，在该方法运行期间，当前事务将被挂起。如果使用JTATransactionManager的话，则需要访问TransactionManager                                                  |
| PROPAGATION_SUPPORTS      | 表示当前方法不需要事务上下文，但是如果存在当前事务的话，那么该方法会在这个事务中运行                                                                                                     |
| PROPAGATION_REQUIRED      | 表示当前方法必须运行在事务中。如果当前事务存在，方法将会在该事务中运行。否则，会启动一个新的事务                                                                                               |
| PROPAGATION_MANDATORY     | 表示该方法必须在事务中运行，如果当前事务不存在，则会抛出一个异常                                                                                                               |
| PROPAGATION_REQUIRED_NEW  | 表示当前方法必须运行在它自己的事务中。一个新的事务将被启动。如果存在当前事务，在该方法执行期间，当前事务会被挂起。如果使用JTATransactionManager的话，则需要访问TransactionManager                                   |
| PROPAGATION_NESTED        | 表示如果当前已经存在一个事务，那么该方法将会在嵌套事务中运行。嵌套的事务可以独立于当前事务进行单独地提交或回滚。如果当前事务不存在，那么其行为与PROPAGATION_REQUIRED一样。注意各厂商对这种传播行为的支持是有所差异的。可以参考资源管理器的文档来确认它们是否支持嵌套事务 |

*   PROPAGATION_REQUIRED(如果存在一个事务，则支持当前事务。如果没有事务则开启一个新的事务。)

    ```
    @Transactional(propagation = Propagation.REQUIRED)
    public void methodA(){
      methodB();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void methodB(){

    }
    ```

    使用spring声明式事务，spring使用AOP来支持声明式事务，会根据事务属性，自动在方法调用之前决定是否开启一个事务，并在方法执行之后决定事务提交或回滚事务。

单独调用methodB方法相当于：

```
Main{ 
    Connection con=null; 
    try{ 
        con = getConnection(); 
        con.setAutoCommit(false); 

        //方法调用
        methodB(); 

        //提交事务
        con.commit(); 
    } Catch(RuntimeException ex) { 
        //回滚事务
        con.rollback();   
    } finally { 
        //释放资源
        closeCon(); 
    } 
}
```

Spring保证在methodB方法中所有的调用都获得到一个相同的连接。在调用methodB时，没有一个存在的事务，所以获得一个新的连接，开启了一个新的事务。 单独调用MethodA时，在MethodA内又会调用MethodB.

执行效果相当于：

```
main{ 
    Connection con = null; 
    try{ 
        con = getConnection(); 
        methodA(); 
        con.commit(); 
    } catch(RuntimeException ex) { 
        con.rollback(); 
    } finally {    
        closeCon(); 
    }  
}
```

调用MethodA时，环境中没有事务，所以开启一个新的事务.当在MethodA中调用MethodB时，环境中已经有了一个事务，所以methodB就加入当前事务。

*   PROPAGATION_SUPPORTS 如果存在一个事务，支持当前事务。如果没有事务，则非事务的执行。但是对于事务同步的事务管理器，PROPAGATION_SUPPORTS与不使用事务有少许不同。

    ```
    @Transactional(propagation = Propagation.REQUIRED)
    public void methodA(){
      methodB();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void methodB(){

    }
    ```

    单纯的调用methodB时，methodB方法是非事务的执行的。当调用methdA时,methodB则加入了methodA的事务中,事务地执行。
*   PROPAGATION_MANDATORY 如果已经存在一个事务，支持当前事务。如果没有一个活动的事务，则抛出异常。

    ```
    @Transactional(propagation = Propagation.REQUIRED)
    public void methodA(){
      methodB();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void methodB(){

    }
    ```

    当单独调用methodB时，因为当前没有一个活动的事务，则会抛出异常throw new IllegalTransactionStateException(“Transaction propagation ‘mandatory’ but no existing transaction found”);当调用methodA时，methodB则加入到methodA的事务中，事务地执行。
*   PROPAGATION_REQUIRES_NEW 总是开启一个新的事务。如果一个事务已经存在，则将这个存在的事务挂起。

    ```
    @Transactional(propagation = Propagation.REQUIRED)
    public void methodA(){
      methodB();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodB(){

    }
    ```

调用A方法相当于：

```
main(){
    TransactionManager tm = null;
    try{
        //获得一个JTA事务管理器
        tm = getTransactionManager();
        tm.begin();//开启一个新的事务
        Transaction ts1 = tm.getTransaction();
        doSomeThing();
        tm.suspend();//挂起当前事务
        try{
            tm.begin();//重新开启第二个事务
            Transaction ts2 = tm.getTransaction();
            methodB();
            ts2.commit();//提交第二个事务
        } Catch(RunTimeException ex) {
            ts2.rollback();//回滚第二个事务
        } finally {
            //释放资源
        }
        //methodB执行完后，恢复第一个事务
        tm.resume(ts1);
        doSomeThingB();
        ts1.commit();//提交第一个事务
    } catch(RunTimeException ex) {
        ts1.rollback();//回滚第一个事务
    } finally {
        //释放资源
    }
}
```

在这里，我把ts1称为外层事务，ts2称为内层事务。从上面的代码可以看出，ts2与ts1是两个独立的事务，互不相干。Ts2是否成功并不依赖于 ts1。如果methodA方法在调用methodB方法后的doSomeThingB方法失败了，而methodB方法所做的结果依然被提交。而除了 methodB之外的其它代码导致的结果却被回滚了。使用PROPAGATION_REQUIRES_NEW,需要使用 JtaTransactionManager作为事务管理器。

* PROPAGATION_NOT_SUPPORTED 总是非事务地执行，并挂起任何存在的事务。使用PROPAGATION_NOT_SUPPORTED,也需要使用JtaTransactionManager作为事务管理器。（代码示例同上，可同理推出）
* PROPAGATION_NEVER 总是非事务地执行，如果存在一个活动事务，则抛出异常。
*   PROPAGATION_NESTED如果一个活动的事务存在，则运行在一个嵌套的事务中. 如果没有活动事务, 则按TransactionDefinition.PROPAGATION_REQUIRED 属性执行。这是一个嵌套事务,使用JDBC 3.0驱动时,仅仅支持DataSourceTransactionManager作为事务管理器。需要JDBC 驱动的java.sql.Savepoint类。有一些JTA的事务管理器实现可能也提供了同样的功能。使用PROPAGATION_NESTED，还需要把PlatformTransactionManager的nestedTransactionAllowed属性设为true;而 nestedTransactionAllowed属性值默认为false。

    ```
    @Transactional(propagation = Propagation.REQUIRED)
    public void methodA(){
      methodB();
    }

    @Transactional(propagation = Propagation.NESTED)
    public void methodB(){

    }
    ```

    如果单独调用methodB方法，则按REQUIRED属性执行。如果调用methodA方法，相当于下面的效果：

    ```
    main(){
      Connection con = null;
      Savepoint savepoint = null;
      try{
          con = getConnection();
          con.setAutoCommit(false);
          doSomeThingA();
          savepoint = con2.setSavepoint();
          try{
              methodB();
          } catch(RuntimeException ex) {
              con.rollback(savepoint);
          } finally {
              //释放资源
          }
          doSomeThingB();
          con.commit();
      } catch(RuntimeException ex) {
          con.rollback();
      } finally {
          //释放资源
      }
    }
    ```

    当methodB方法调用之前，调用setSavepoint方法，保存当前的状态到savepoint。如果methodB方法调用失败，则恢复到之前保存的状态。但是需要注意的是，这时的事务并没有进行提交，如果后续的代码(doSomeThingB()方法)调用失败，则回滚包括methodB方法的所有操作。

嵌套事务一个非常重要的概念就是内层事务依赖于外层事务。外层事务失败时，会回滚内层事务所做的动作。而内层事务操作失败并不会引起外层事务的回滚。

PROPAGATION_NESTED 与PROPAGATION_REQUIRES_NEW的区别:它们非常类似,都像一个嵌套事务，如果不存在一个活动的事务，都会开启一个新的事务。使用 PROPAGATION_REQUIRES_NEW时，内层事务与外层事务就像两个独立的事务一样，一旦内层事务进行了提交后，外层事务不能对其进行回滚。两个事务互不影响。两个事务不是一个真正的嵌套事务。同时它需要JTA事务管理器的支持。

使用PROPAGATION_NESTED时，外层事务的回滚可以引起内层事务的回滚。而内层事务的异常并不会导致外层事务的回滚，它是一个真正的嵌套事务。DataSourceTransactionManager使用savepoint支持PROPAGATION_NESTED时，需要JDBC 3.0以上驱动及1.4以上的JDK版本支持。其它的JTA TrasactionManager实现可能有不同的支持方式。

PROPAGATION_REQUIRES_NEW 启动一个新的, 不依赖于环境的 “内部” 事务. 这个事务将被完全 commited 或 rolled back 而不依赖于外部事务, 它拥有自己的隔离范围, 自己的锁, 等等. 当内部事务开始执行时, 外部事务将被挂起, 内务事务结束时, 外部事务将继续执行。

另一方面, PROPAGATION_NESTED 开始一个 “嵌套的” 事务, 它是已经存在事务的一个真正的子事务. 潜套事务开始执行时, 它将取得一个 savepoint. 如果这个嵌套事务失败, 我们将回滚到此 savepoint. 潜套事务是外部事务的一部分, 只有外部事务结束后它才会被提交。

由此可见, PROPAGATION_REQUIRES_NEW 和 PROPAGATION_NESTED 的最大区别在于, PROPAGATION_REQUIRES_NEW 完全是一个新的事务, 而 PROPAGATION_NESTED 则是外部事务的子事务, 如果外部事务 commit, 嵌套事务也会被 commit, 这个规则同样适用于 roll back.

PROPAGATION_REQUIRED应该是我们首先的事务传播行为。它能够满足我们大多数的事务需求。

#### 2.2.2 隔离级别

[MySQL隔离级别](https://github.com/QQ1350995917/gitbook/tree/1301eb5173842b2e1ed6ae91795462f32063b906/spring/storage/databases/mysql/chapter05.md)

事务的第二个维度就是隔离级别（isolation level）。隔离级别定义了一个事务可能受其他并发事务影响的程度。 并发事务引起的问题,在典型的应用程序中，多个事务并发运行，经常会操作相同的数据来完成各自的任务。并发虽然是必须的，但可能会导致一下的问题。

* 脏读（Dirty reads）——脏读发生在一个事务读取了另一个事务改写但尚未提交的数据时。如果改写在稍后被回滚了，那么第一个事务获取的数据就是无效的。
* 不可重复读（Nonrepeatable read）——不可重复读发生在一个事务执行相同的查询两次或两次以上，但是每次都得到不同的数据时。这通常是因为另一个并发事务在两次查询期间进行了更新。
* 幻读（Phantom read）——幻读与不可重复读类似。它发生在一个事务（T1）读取了几行数据，接着另一个并发事务（T2）插入了一些数据时。在随后的查询中，第一个事务（T1）就会发现多了一些原本不存在的记录。

不可重复读与幻读的区别

不可重复读的重点是修改: 同样的条件, 你读取过的数据, 再次读取出来发现值不一样了

例如：在事务1中，Mary 读取了自己的工资为1000,操作并没有完成

```
    con1 = getConnection();  
    select salary from employee empId ="Mary";
```

在事务2中，这时财务人员修改了Mary的工资为2000,并提交了事务.

```
    con2 = getConnection();  
    update employee set salary = 2000;  
    con2.commit();
```

在事务1中，Mary 再次读取自己的工资时，工资变为了2000

```
   select salary from employee empId ="Mary";
```

在一个事务中前后两次读取的结果并不一致，导致了不可重复读。

幻读的重点在于新增或者删除：

同样的条件, 第1次和第2次读出来的记录数不一样

例如：目前工资为1000的员工有10人。事务1,读取所有工资为1000的员工。

```
    con1 = getConnection();  
    Select * from employee where salary =1000;
```

共读取10条记录

这时另一个事务向employee表插入了一条员工记录，工资也为1000

```
    con2 = getConnection();  
    Insert into employee(empId,salary) values("Lili",1000);  
    con2.commit();
```

事务1再次读取所有工资为1000的员工

```
    select * from employee where salary =1000;
```

共读取到了11条记录，这就产生了幻像读。

从总的结果来看, 似乎不可重复读和幻读都表现为两次读取的结果不一致。但如果你从控制的角度来看, 两者的区别就比较大。 对于前者, 只需要锁住满足条件的记录。 对于后者, 要锁住满足条件及其相近的记录。

隔离级别

| 隔离级别                       | 含义                                                                           |
| -------------------------- | ---------------------------------------------------------------------------- |
| ISOLATION_DEFAULT          | 使用后端数据库默认的隔离级别                                                               |
| ISOLATION_READ_UNCOMMITTED | 最低的隔离级别，允许读取尚未提交的数据变更，可能会导致脏读、幻读或不可重复读                                       |
| ISOLATION_READ_COMMITTED   | 允许读取并发事务已经提交的数据，可以阻止脏读，但是幻读或不可重复读仍有可能发生                                      |
| ISOLATION_REPEATABLE_READ  | 对同一字段的多次读取结果都是一致的，除非数据是被本身事务自己所修改，可以阻止脏读和不可重复读，但幻读仍有可能发生                     |
| ISOLATION_SERIALIZABLE     | 最高的隔离级别，完全服从ACID的隔离级别，确保阻止脏读、不可重复读以及幻读，也是最慢的事务隔离级别，因为它通常是通过完全锁定事务相关的数据库表来实现的 |

#### 2.2.3 只读

事务的第三个特性是它是否为只读事务。如果事务只对后端的数据库进行该操作，数据库可以利用事务的只读特性来进行一些特定的优化。通过将事务设置为只读，你就可以给数据库一个机会，让它应用它认为合适的优化措施。

#### 2.2.4 事务超时

为了使应用程序很好地运行，事务不能运行太长的时间。因为事务可能涉及对后端数据库的锁定，所以长时间的事务会不必要的占用数据库资源。事务超时就是事务的一个定时器，在特定时间内事务如果没有执行完毕，那么就会自动回滚，而不是一直等待其结束。

#### 2.2.5 回滚规则

事务五边形的最后一个方面是一组规则，这些规则定义了哪些异常会导致事务回滚而哪些不会。默认情况下，事务只有遇到运行期异常时才会回滚，而在遇到检查型异常时不会回滚（这一行为与EJB的回滚行为是一致的） 但是你可以声明事务在遇到特定的检查型异常时像遇到运行期异常那样回滚。同样，你还可以声明事务遇到特定的异常不回滚，即使这些异常是运行期异常。

### 2.3 事务状态

上面讲到的调用PlatformTransactionManager接口的getTransaction()的方法得到的是TransactionStatus接口的一个实现，这个接口的内容如下：

```
public interface TransactionStatus{
    boolean isNewTransaction(); // 是否是新的事物
    boolean hasSavepoint(); // 是否有恢复点
    void setRollbackOnly();  // 设置为只回滚
    boolean isRollbackOnly(); // 是否为只回滚
    boolean isCompleted; // 是否已完成
}
```

可以发现这个接口描述的是一些处理事务提供简单的控制事务执行和查询事务状态的方法，在回滚或提交的时候需要应用对应的事务状态。

### 3 编程式事务

#### 3.1 编程式和声明式事务的区别

Spring提供了对编程式事务和声明式事务的支持，编程式事务允许用户在代码中精确定义事务的边界，而声明式事务（基于AOP）有助于用户将操作与事务规则进行解耦。 简单地说，编程式事务侵入到了业务代码里面，但是提供了更加详细的事务管理；而声明式事务由于基于AOP，所以既能起到事务管理的作用，又可以不影响业务代码的具体实现。

#### 3.2 如何实现编程式事务？

Spring提供两种方式的编程式事务管理，分别是：使用TransactionTemplate和直接使用PlatformTransactionManager。

#### 3.2.1 使用TransactionTemplate

采用TransactionTemplate和采用其他Spring模板，如JdbcTempalte和HibernateTemplate是一样的方法。它使用回调方法，把应用程序从处理取得和释放资源中解脱出来。如同其他模板，TransactionTemplate是线程安全的。代码片段：

```
    TransactionTemplate tt = new TransactionTemplate(); // 新建一个TransactionTemplate
    Object result = tt.execute(
        new TransactionCallback(){  
            public Object doTransaction(TransactionStatus status){  
                updateOperation();  
                return resultOfUpdateOperation();  
            }  
    }); // 执行execute方法进行事务管理
```

使用TransactionCallback()可以返回一个值。如果使用TransactionCallbackWithoutResult则没有返回值。

#### 3.2.2 使用PlatformTransactionManager

```
    DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(); //定义一个某个框架平台的TransactionManager，如JDBC、Hibernate
    dataSourceTransactionManager.setDataSource(this.getJdbcTemplate().getDataSource()); // 设置数据源
    DefaultTransactionDefinition transDef = new DefaultTransactionDefinition(); // 定义事务属性
    transDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED); // 设置传播行为属性
    TransactionStatus status = dataSourceTransactionManager.getTransaction(transDef); // 获得事务状态
    try {
        // 数据库操作
        dataSourceTransactionManager.commit(status);// 提交
    } catch (Exception e) {
        dataSourceTransactionManager.rollback(status);// 回滚
    }
```

### 4 声明式事务

#### 4.1 配置方式

根据代理机制的不同，总结了五种Spring事务的配置方式，配置文件如下：

*   每个Bean都有一个代理

    ```
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
               http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
               http://www.springframework.org/schema/context
               http://www.springframework.org/schema/context/spring-context-2.5.xsd
               http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">

        <bean id="sessionFactory" 
                class="org.springframework.orm.hibernate3.LocalSessionFactoryBean"> 
            <property name="configLocation" value="classpath:hibernate.cfg.xml" /> 
            <property name="configurationClass" value="org.hibernate.cfg.AnnotationConfiguration" />
        </bean> 

        <!-- 定义事务管理器（声明式的事务） --> 
        <bean id="transactionManager"
            class="org.springframework.orm.hibernate3.HibernateTransactionManager">
            <property name="sessionFactory" ref="sessionFactory" />
        </bean>

        <!-- 配置DAO -->
        <bean id="userDaoTarget" class="com.bluesky.spring.dao.UserDaoImpl">
            <property name="sessionFactory" ref="sessionFactory" />
        </bean>

        <bean id="userDao" 
            class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean"> 
               <!-- 配置事务管理器 --> 
               <property name="transactionManager" ref="transactionManager" />    
            <property name="target" ref="userDaoTarget" /> 
             <property name="proxyInterfaces" value="com.bluesky.spring.dao.GeneratorDao" />
            <!-- 配置事务属性 --> 
            <property name="transactionAttributes"> 
                <props> 
                    <prop key="*">PROPAGATION_REQUIRED</prop>
                </props> 
            </property> 
        </bean> 
    </beans>
    ```
*   所有Bean共享一个代理基类

    ```
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
               http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
               http://www.springframework.org/schema/context
               http://www.springframework.org/schema/context/spring-context-2.5.xsd
               http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">

        <bean id="sessionFactory" 
                class="org.springframework.orm.hibernate3.LocalSessionFactoryBean"> 
            <property name="configLocation" value="classpath:hibernate.cfg.xml" /> 
            <property name="configurationClass" value="org.hibernate.cfg.AnnotationConfiguration" />
        </bean> 

        <!-- 定义事务管理器（声明式的事务） --> 
        <bean id="transactionManager"
            class="org.springframework.orm.hibernate3.HibernateTransactionManager">
            <property name="sessionFactory" ref="sessionFactory" />
        </bean>

        <bean id="transactionBase" 
                class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean" 
                lazy-init="true" abstract="true"> 
            <!-- 配置事务管理器 --> 
            <property name="transactionManager" ref="transactionManager" /> 
            <!-- 配置事务属性 --> 
            <property name="transactionAttributes"> 
                <props> 
                    <prop key="*">PROPAGATION_REQUIRED</prop> 
                </props> 
            </property> 
        </bean>   

        <!-- 配置DAO -->
        <bean id="userDaoTarget" class="com.bluesky.spring.dao.UserDaoImpl">
            <property name="sessionFactory" ref="sessionFactory" />
        </bean>

        <bean id="userDao" parent="transactionBase" > 
            <property name="target" ref="userDaoTarget" />  
        </bean>
    </beans>
    ```
*   使用拦截器

    ```
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
               http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
               http://www.springframework.org/schema/context
               http://www.springframework.org/schema/context/spring-context-2.5.xsd
               http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">

        <bean id="sessionFactory" 
                class="org.springframework.orm.hibernate3.LocalSessionFactoryBean"> 
            <property name="configLocation" value="classpath:hibernate.cfg.xml" /> 
            <property name="configurationClass" value="org.hibernate.cfg.AnnotationConfiguration" />
        </bean> 

        <!-- 定义事务管理器（声明式的事务） --> 
        <bean id="transactionManager"
            class="org.springframework.orm.hibernate3.HibernateTransactionManager">
            <property name="sessionFactory" ref="sessionFactory" />
        </bean> 

        <bean id="transactionInterceptor" 
            class="org.springframework.transaction.interceptor.TransactionInterceptor"> 
            <property name="transactionManager" ref="transactionManager" /> 
            <!-- 配置事务属性 --> 
            <property name="transactionAttributes"> 
                <props> 
                    <prop key="*">PROPAGATION_REQUIRED</prop> 
                </props> 
            </property> 
        </bean>

        <bean class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator"> 
            <property name="beanNames"> 
                <list> 
                    <value>*Dao</value>
                </list> 
            </property> 
            <property name="interceptorNames"> 
                <list> 
                    <value>transactionInterceptor</value> 
                </list> 
            </property> 
        </bean> 

        <!-- 配置DAO -->
        <bean id="userDao" class="com.bluesky.spring.dao.UserDaoImpl">
            <property name="sessionFactory" ref="sessionFactory" />
        </bean>
    </beans>
    ```
*   使用tx标签配置的拦截器

    ```
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xmlns:tx="http://www.springframework.org/schema/tx"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
               http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
               http://www.springframework.org/schema/context
               http://www.springframework.org/schema/context/spring-context-2.5.xsd
               http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd
               http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">

        <context:annotation-config />
        <context:component-scan base-package="com.bluesky" />

        <bean id="sessionFactory" 
                class="org.springframework.orm.hibernate3.LocalSessionFactoryBean"> 
            <property name="configLocation" value="classpath:hibernate.cfg.xml" /> 
            <property name="configurationClass" value="org.hibernate.cfg.AnnotationConfiguration" />
        </bean> 

        <!-- 定义事务管理器（声明式的事务） --> 
        <bean id="transactionManager"
            class="org.springframework.orm.hibernate3.HibernateTransactionManager">
            <property name="sessionFactory" ref="sessionFactory" />
        </bean>

        <tx:advice id="txAdvice" transaction-manager="transactionManager">
            <tx:attributes>
                <tx:method name="*" propagation="REQUIRED" />
            </tx:attributes>
        </tx:advice>

        <aop:config>
            <aop:pointcut id="interceptorPointCuts"
                expression="execution(* com.bluesky.spring.dao.*.*(..))" />
            <aop:advisor advice-ref="txAdvice"
                pointcut-ref="interceptorPointCuts" />       
        </aop:config>     
    </beans>
    ```
*   全注解

    ```
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xmlns:tx="http://www.springframework.org/schema/tx"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
               http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
               http://www.springframework.org/schema/context
               http://www.springframework.org/schema/context/spring-context-2.5.xsd
               http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd
               http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">

        <context:annotation-config />
        <context:component-scan base-package="com.bluesky" />

        <tx:annotation-driven transaction-manager="transactionManager"/>

        <bean id="sessionFactory" 
                class="org.springframework.orm.hibernate3.LocalSessionFactoryBean"> 
            <property name="configLocation" value="classpath:hibernate.cfg.xml" /> 
            <property name="configurationClass" value="org.hibernate.cfg.AnnotationConfiguration" />
        </bean> 

        <!-- 定义事务管理器（声明式的事务） --> 
        <bean id="transactionManager"
            class="org.springframework.orm.hibernate3.HibernateTransactionManager">
            <property name="sessionFactory" ref="sessionFactory" />
        </bean>

    </beans>
    ```

    此时在DAO上需加上@Transactional注解，如下：

    ```
    @Transactional
    @Component("userDao")
    public class UserDaoImpl extends HibernateDaoSupport implements UserDao {

        public List<User> listUsers() {
            return this.getSession().createQuery("from User").list();
        }  
    }
    ```

    **参考**

    [https://blog.csdn.net/justry_deng/article/details/80828180](https://blog.csdn.net/justry_deng/article/details/80828180)
