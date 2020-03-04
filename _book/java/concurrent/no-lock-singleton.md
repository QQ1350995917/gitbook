# 如何实现一个线程安全的单例，前提是不能加锁
## 可以使用饿汉模式实现单例。如：
```
public class Singleton { 

    private static Singleton instance = new Singleton();

    private Singleton (){}

    public static Singleton getInstance() {

      return instance;

    }
}
```
## 饿汉的变种：
使用static来定义静态成员变量或静态代码，借助Class的类加载机制实现线程安全单例
```
public class Singleton {

    private Singleton instance = null;

    static {
        instance = new Singleton();
    }

    private Singleton (){}

    public static Singleton getInstance() {
        return this.instance;
    }
}
```

## 通过静态内部类来实现
这种方式相比前面两种有所优化，就是使用了lazy-loading。Singleton类被装载了，但是instance并没有立即初始化。因为SingletonHolder类没有被主动使用，只有显示通过调用getInstance方法时，才会显示装载SingletonHolder类，从而实例化instance。

```
public class Singleton {

    private static class SingletonHolder {

    private static final Singleton INSTANCE = new Singleton();

}

private Singleton (){}

public static final Singleton getInstance() {

return SingletonHolder.INSTANCE;

}

}

```
## 枚举的方式
这种方式是Effective Java作者Josh Bloch 提倡的方式，它不仅能避免多线程同步问题，而且还能防止反序列化重新创建新的对象，可谓是很坚强的壁垒。

面试官：以上几种答案，其实现原理都是利用借助了类加载的时候初始化单例。即借助了ClassLoader的线程安全机制。

所谓ClassLoader的线程安全机制，就是ClassLoader的loadClass方法在加载类的时候使用了synchronized关键字。也正是因为这样， 除非被重写，这个方法默认在整个装载过程中都是同步的，也就是保证了线程安全。

所以，以上各种方法，虽然并没有显示的使用synchronized，但是还是其底层实现原理还是用到了synchronized。

```
public enum Singleton {

INSTANCE;

    public void whateverMethod() {

}

}
```
## CAS
CAS是项乐观锁技术，当多个线程尝试使用CAS同时更新同一个变量时，只有其中一个线程能更新变量的值，而其它线程都失败，失败的线程并不会被挂起，而是被告知这次竞争中失败，并可以再次尝试。实现单例的方式如下：
```
public class Singleton {

    private static final AtomicReference INSTANCE = new AtomicReference();

    private Singleton() {}

    public static Singleton getInstance() {

        for (;;) {

Singleton singleton = INSTANCE.get();

            if (null != singleton) {

                return singleton;

            }

singleton = new Singleton();

            if (INSTANCE.compareAndSet(null, singleton)) {

                return singleton;

            }

        }

    }

}
```
用CAS的好处在于不需要使用传统的锁机制来保证线程安全,CAS是一种基于忙等待的算法,依赖底层硬件的实现,相对于锁它没有线程切换和阻塞的额外消耗,可以支持较大的并行度。

CAS的一个重要缺点在于如果忙等待一直执行不成功(一直在死循环中),会对CPU造成较大的执行开销。

另外，如果N个线程同时执行到singleton = new Singleton();的时候，会有大量对象创建，很可能导致内存溢出。

## 参考
https://www.jianshu.com/p/f3fae8658f13
