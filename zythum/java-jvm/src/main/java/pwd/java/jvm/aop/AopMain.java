package pwd.java.jvm.aop;

import java.lang.reflect.Proxy;

/**
 * pwd.java.jvm.aop@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2021-03-17 16:42
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class AopMain {
    public static void main(String[] args) {
        AopImplement aopImplement = new AopImplement();
        AopHandler handle = new AopHandler(aopImplement);
        AopInterface i = (AopInterface) Proxy.newProxyInstance(AopImplement.class.getClassLoader(), new Class[] { AopInterface.class }, handle);
        i.hi();
    }
}
