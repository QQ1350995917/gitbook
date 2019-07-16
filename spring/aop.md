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

