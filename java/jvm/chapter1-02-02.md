# 直接内存

直接内存（Direct Memory）并不是虚拟机运行时数据区的一部分，也不是Java虚拟机规范中定义的内存区域，但是这部分内存也被频繁的使用，二期也可能导致OutOfMemoryError异常出现。

在JDK1.4中新加入了NIO（New Input/Output）类，引入了一种基于通道（Channel）与缓冲区（Buffer)的I/O方式，他可以使用Native函数库直接分配对外内存，然后通过一个存储在Java堆中的DirectByteBuffer对象作为这块内存的引用进行操作。这样能在一些场景中显著的提高性能，因为避免了在Java堆中和Native堆中来回复制数据。

显然，本机直接内存的分配不会收到Java堆大小的限制，但是，既然是内存，则肯定还是会受到本机总内存（包括RAM以及Swap区或者分页文件）的大小以及处理器寻址空间的限制。服务器管理员配置虚拟机参数时，一般会根据实际内存设置-Xmx等参数信息，单经常会忽略掉直接内存，使得各个内存区域的总和大于物理内存限制（包括物理上的和操作系统级的限制），从而导致动态扩展时出现OutOfMemoryError异常。

可以通过 -XX:MaxDirectMemorySize 参数来设置最大可用直接内存，如果启动时未设置则[JDK8默认为64M](https://github.com/frohoff/jdk8u-dev-jdk/blob/master/src/share/classes/sun/misc/VM.java#L186)。也可以设置为-1表示和堆内存一样大。
```java
    // A user-settable upper limit on the maximum amount of allocatable direct
    // buffer memory.  This value may be changed during VM initialization if
    // "java" is launched with "-XX:MaxDirectMemorySize=<size>".
    //
    // The initial value of this field is arbitrary; during JRE initialization
    // it will be reset to the value specified on the command line, if any,
    // otherwise to Runtime.getRuntime().maxMemory().
    //
    private static long directMemory = 64 * 1024 * 1024;

    // Set the maximum amount of direct memory.  This value is controlled
    // by the vm option -XX:MaxDirectMemorySize=<size>.
    // The maximum amount of allocatable direct buffer memory (in bytes)
    // from the system property sun.nio.MaxDirectMemorySize set by the VM.
    // The system property will be removed.
    String s = (String)props.remove("sun.nio.MaxDirectMemorySize");
    if (s != null) {
        if (s.equals("-1")) {
           // -XX:MaxDirectMemorySize not given, take default
            directMemory = Runtime.getRuntime().maxMemory();
        } else {
           long l = Long.parseLong(s);
            if (l > -1)
                directMemory = l;
        }
    }
```
当直接内存达到最大限制时就会触发GC，如果回收失败则会引起OutOfMemoryError。


环境为JDK9，两种内存分配的耗时如下，运行两遍让其预热。可以看到直接内存的分配比较耗时，而堆内存分配操作耗时少好几倍。

```java
public static void directMemoryAllocate() {
		long tsStart = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			ByteBuffer buffer = ByteBuffer.allocateDirect(400);

		}
		System.out.println("direct memory allocate: " + (System.currentTimeMillis() - tsStart) + " ms");
		tsStart = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			ByteBuffer buffer = ByteBuffer.allocate(400);
		}
		System.out.println("heap memory allocate： " + (System.currentTimeMillis() - tsStart) + " ms");
	}

```

> direct memory allocate: 149 ms
> heap memory allocate： 41 ms
> direct memory allocate: 122 ms
> heap memory allocate： 31 ms

环境为JDK9，两种内存的读写操作耗时如下，同样运行两遍让其预热，可以看到直接内存读写操作的速度相对快一些。

```java
public static void memoryRW() {
		ByteBuffer buffer = ByteBuffer.allocateDirect(400);
		ByteBuffer buffer2 = ByteBuffer.allocate(400);
		long tsStart = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			for (int j = 0; j < 100; j++) {
				buffer.putInt(j);
			}
			buffer.flip();
			for (byte j = 0; j < 100; j++) {
				buffer.getInt();
			}
			buffer.clear();
		}
		System.out.println("direct memory rw： " + (System.currentTimeMillis() - tsStart) + " ms");

		tsStart = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			for (int j = 0; j < 100; j++) {
				buffer2.putInt(j);
			}
			buffer2.flip();
			for (byte j = 0; j < 100; j++) {
				buffer2.getInt();
			}
			buffer2.clear();
		}
		System.out.println("heap memory rw： " + (System.currentTimeMillis() - tsStart) + " ms");
	}

```
> direct memory rw： 39 ms
> heap memory rw： 34 ms
> direct memory rw： 23 ms
> heap memory rw： 46 ms

理论上直接内存的机制访问速度要快一些，但也不能武断地直接说直接内存快，另外，在内存分配操作上直接内存要慢一些。直接内存更适合在内存申请次数较少，但读写操作较频繁的场景。

