org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean
doGetBean的定义如下
```java
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {
	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * @param name the name of the bean to retrieve
	 * @param requiredType the required type of the bean to retrieve
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @param typeCheckOnly whether the instance is obtained for a type check,
	 * not for actual use
	 * @return an instance of the bean
	 * @throws BeansException if the bean could not be created
	 */
	@SuppressWarnings("unchecked")
	protected <T> T doGetBean(final String name, @Nullable final Class<T> requiredType,
			@Nullable final Object[] args, boolean typeCheckOnly) throws BeansException {
	  // 1：根据BeanName检查缓存中是否有已经注册的单例，对循环引用的处理逻辑进行了处理
	  // 2: 如果没有上述的单例被找到，检查definition是否已经在parentBeanFactory中是否已注册，如果找到就根据类型，参数等信息进行却别返回
	  // 3: 如果没收上述的bean被找到，则往下判断当前bean依赖的bean是否已经被初始化，
	  // 4: 如果依赖没有初始化则递归初始化
	  // 5: 如果依赖初始化已经完成则判断当前的bean是单例原型，根据不同的类型进行初始化，都不是的话根据scopeName进行初始化
	  // 6: 检查所需类型是否与实际bean实例的类型匹配，匹配上则返回，否则抛出异常
	  // 7: 根据实例对象获取bean对象的方法是首先使用org.springframework.beans.factory.support.AbstractBeanFactory.createBean
	  // 8：createBean有异常则使用org.springframework.beans.factory.support.AbstractBeanFactory.getObjectForBeanInstance，
	  // 9: getObjectForBeanInstance中大部分还是返回instance本身，条件是beanInstance instanceof NullBean
	  // 10: createBean 由子类org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])实现
	  // 11: createBean 主要调用org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean
	  // 12: doCreateBean处理了循环引用的逻辑，初始化bean的过程主要封装在org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition) 
	}
}
```
```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
		implements AutowireCapableBeanFactory {
  	/**
  	 * Initialize the given bean instance, applying factory callbacks
  	 * as well as init methods and bean post processors.
  	 * <p>Called from {@link #createBean} for traditionally defined beans,
  	 * and from {@link #initializeBean} for existing bean instances.
  	 * @param beanName the bean name in the factory (for debugging purposes)
  	 * @param bean the new bean instance we may need to initialize
  	 * @param mbd the bean definition that the bean was created with
  	 * (can also be {@code null}, if given an existing bean instance)
  	 * @return the initialized bean instance (potentially wrapped)
  	 * @see BeanNameAware
  	 * @see BeanClassLoaderAware
  	 * @see BeanFactoryAware
  	 * @see #applyBeanPostProcessorsBeforeInitialization
  	 * @see #invokeInitMethods
  	 * @see #applyBeanPostProcessorsAfterInitialization
  	 */
  	protected Object initializeBean(final String beanName, final Object bean, @Nullable RootBeanDefinition mbd) {
  		if (System.getSecurityManager() != null) {
  			AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
  				invokeAwareMethods(beanName, bean);
  				return null;
  			}, getAccessControlContext());
  		}
  		else {
  			invokeAwareMethods(beanName, bean);
  		}
  
  		Object wrappedBean = bean;
  		if (mbd == null || !mbd.isSynthetic()) {
  			wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
  		}
  
  		try {
  			invokeInitMethods(beanName, wrappedBean, mbd);
  		}
  		catch (Throwable ex) {
  			throw new BeanCreationException(
  					(mbd != null ? mbd.getResourceDescription() : null),
  					beanName, "Invocation of init method failed", ex);
  		}
  		if (mbd == null || !mbd.isSynthetic()) {
  			wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
  		}
  
  		return wrappedBean;
  	}
  	// applyBeanPostProcessorsAfterInitialization 最终完成AOP初始化
  	@Override
  	public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
  			throws BeansException {
  
  		Object result = existingBean;
  		// 在getBeanPostProcessors集合中包含了org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator
  		// org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator是
  		for (BeanPostProcessor processor : getBeanPostProcessors()) {
  			Object current = processor.postProcessAfterInitialization(result, beanName);
  			if (current == null) {
  				return result;
  			}
  			result = current;
  		}
  		return result;
  	}  
}
```

org.springframework.aop.framework.DefaultAopProxyFactory
org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.postProcessAfterInitialization


```java
@SuppressWarnings("serial")
public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {
	@Override
	public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
		if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
			Class<?> targetClass = config.getTargetClass();
			if (targetClass == null) {
				throw new AopConfigException("TargetSource cannot determine target class: " +
						"Either an interface or a target is required for proxy creation.");
			}
			if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
				return new JdkDynamicAopProxy(config);
			}
			return new ObjenesisCglibAopProxy(config);
		}
		else {
			return new JdkDynamicAopProxy(config);
		}
	}

}
```



- 根据beanName获取单例实例
  org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(java.lang.String, boolean)  
  此时获取到的实例有可能是循环引用中的早期引用  
  通过org.springframework.beans.factory.support.AbstractBeanFactory.getObjectForBeanInstance返回一个bean对象  
  这个方法中判断了FactoryBean，BeanFactory,以及本身的bean对象，最后返回一个获取的Bean对象。
  ```java
  public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {
    
    /** Cache of singleton objects: bean name to bean instance. */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
  	/** Cache of singleton factories: bean name to ObjectFactory. */
  	private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
  	/** Cache of early singleton objects: bean name to bean instance. */
  	private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
		/** Names of beans that are currently in creation. */
  	private final Set<String> singletonsCurrentlyInCreation =
  			Collections.newSetFromMap(new ConcurrentHashMap<>(16));
    /**
     * Return the (raw) singleton object registered under the given name.
     * <p>Checks already instantiated singletons and also allows for an early
     * reference to a currently created singleton (resolving a circular reference).
     * @param beanName the name of the bean to look for
     * @param allowEarlyReference whether early references should be created or not
     * @return the registered singleton object, or {@code null} if none found
     */
    @Nullable
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
      Object singletonObject = this.singletonObjects.get(beanName);
      if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
        synchronized (this.singletonObjects) {
          singletonObject = this.earlySingletonObjects.get(beanName);
          if (singletonObject == null && allowEarlyReference) {
            ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
            if (singletonFactory != null) {
              singletonObject = singletonFactory.getObject();
              this.earlySingletonObjects.put(beanName, singletonObject);
              this.singletonFactories.remove(beanName);
            }
          }
        }
      }
      return singletonObject;
    }
  
  	/**
  	 * Return whether the specified singleton bean is currently in creation
  	 * (within the entire factory).
  	 * @param beanName the name of the bean
  	 */
  	public boolean isSingletonCurrentlyInCreation(String beanName) {
  		return this.singletonsCurrentlyInCreation.contains(beanName);
  	}
  }

  ```
