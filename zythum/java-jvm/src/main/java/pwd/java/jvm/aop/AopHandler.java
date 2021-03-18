package pwd.java.jvm.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * pwd.java.jvm.aop@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2021-03-17 16:39
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class AopHandler implements InvocationHandler {
    private Object obj;

    AopHandler(Object obj){
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //方法返回值
        System.out.println("前置代理");
        //反射调用方法
        Object ret=method.invoke(obj, args);
        //声明结束
        System.out.println("后置代理");
        //返回反射调用方法的返回值
        return ret;
    }
}
