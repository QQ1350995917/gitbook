# JVM内存性能调优常用指令

## jps(Java Virtual Machine Process Status Tool)

java提供的一个显示当前所有java进程pid的命令，适合在linux/unix平台上简单察看当前java进程的一些简单情况。显示当前系统的java进程情况及进程id。

```
usage: jps [-help]
       jps [-q] [-mlvV] [<hostid>]

Definitions:
    <hostid>:      <hostname>[:<port>]
PWD:workspace pwd$ jps -help
usage: jps [-help]
       jps [-q] [-mlvV] [<hostid>]

Definitions:
    <hostid>:      <hostname>[:<port>]
```

* \-q 只显示pid，不显示class名称,jar文件名和传递给main方法的参数
* \-m 输出传递给main方法的参数，在嵌入式jvm上可能是null
* \-l 输出应用程序main class的完整package名或者应用程序的jar文件完整路径名
* \-v 输出传递给JVM的参数
* \-V 隐藏输出传递给JVM的参数

## jinfo（Configuration Info for Java）

作用是实时地查看和调整虚拟机各项参数。包括Java System属性和JVM命令行参数也可以动态的修改正在运行的 JVM 一些参数。当系统崩溃时，jinfo可以从core文件里面知道崩溃的Java应用程序的配置信息

```
Usage:
    jinfo [option] <pid>
        (to connect to running process)
    jinfo [option] <executable <core>
        (to connect to a core file)
    jinfo [option] [server_id@]<remote server IP or hostname>
        (to connect to remote debug server)

where <option> is one of:
    -flag <name>         to print the value of the named VM flag
    -flag [+|-]<name>    to enable or disable the named VM flag
    -flag <name>=<value> to set the named VM flag to the given value
    -flags               to print VM flags
    -sysprops            to print Java system properties
    <no option>          to print both of the above
    -h | -help           to print this help message
```

### 参数说明

* pid 对应jvm的进程id
* executable core 产生javacore(javadump)文件
* \[server-id@]remote server IP or hostname 远程的ip或者hostname，server-id标记服务的唯一性id

### option

* \-no option 输出全部的参数和系统属性
* \-flag name 输出对应名称的参数，使用该命令，可以查看指定的 jvm 参数的值。如：查看当前 jvm 进程是否开启打印 GC 日志。
* \-flag \[+|-]name 开启或者关闭对应名称的参数，主要是针对 boolean 值的参数设置的，可以在不重启虚拟机的情况下，可以动态的修改 jvm 的参数。尤其在线上的环境特别有用。
* \-flag name=value 设定对应名称的参数，设置 value值，则需要使用 name=value 的形式。jinfo虽然可以在java程序运行时动态地修改虚拟机参数，但并不是所有的参数都支持动态修改。
* \-flags 输出全部的参数
* \-sysprops 输出系统属性 ，输出当前 jvm 进行的全部的系统属性

### Javacore 概述

Javacore，也可以称为“threaddump”或是“javadump”，它是 Java 提供的一种诊断特性，能够提供一份可读的当前运行的 JVM 中线程使用情况的快照。即在某个特定时刻，JVM 中有哪些线程在运行，每个线程执行到哪一个类，哪一个方法。应用程序如果出现不可恢复的错误或是内存泄露，就会自动触发 Javacore 的生成。

## jstat（Java Virtual Machine statistics monitoring tool）

主要利用JVM内建的指令对Java应用程序的资源和性能进行实时的命令行的监控，包括了对Heap size和垃圾回收状况的监控。

```
jstat -help

Usage: jstat -help|-options
       jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]

Definitions:
  <option>      An option reported by the -options option
  <vmid>        Virtual Machine Identifier. A vmid takes the following form:
                     <lvmid>[@<hostname>[:<port>]]
                Where <lvmid> is the local vm identifier for the target
                Java virtual machine, typically a process id; <hostname> is
                the name of the host running the target Java virtual machine;
                and <port> is the port number for the rmiregistry on the
                target host. See the jvmstat documentation for a more complete
                description of the Virtual Machine Identifier.
  <lines>       Number of samples between header lines.
  <interval>    Sampling interval. The following forms are allowed:
                    <n>["ms"|"s"]
                Where <n> is an integer and the suffix specifies the units as 
                milliseconds("ms") or seconds("s"). The default units are "ms".
  <count>       Number of samples to take before terminating.
  -J<flag>      Pass <flag> directly to the runtime system.

  jstat -options

  -class
  -compiler
  -gc
  -gccapacity
  -gccause
  -gcmetacapacity
  -gcnew
  -gcnewcapacity
  -gcold
  -gcoldcapacity
  -gcutil
  -printcompilation
```

* options： 参数选项
* \-t： 可以在打印的列加上Timestamp列，用于显示系统运行的时间
* \-h： 可以在周期性数据数据的时候，可以在指定输出多少行以后输出一次表头
* vmid： Virtual Machine ID（ 进程的 pid）
* interval： 执行每次的间隔时间，单位为毫秒
* count： 用于指定输出多少次记录，缺省则会一直打印

option 可以从下面参数中选择

* \-class 显示ClassLoad的相关信息；
* \-compiler 显示JIT编译的相关信息；
* \-gc 显示和gc相关的堆信息；
* \-gccapacity 　　 显示各个代的容量以及使用情况；
* \-gcmetacapacity 显示metaspace的大小
* \-gcnew 显示新生代信息；
* \-gcnewcapacity 显示新生代大小和使用情况；
* \-gcold 显示老年代和永久代的信息；
* \-gcoldcapacity 显示老年代的大小；
* \-gcutil　　 显示垃圾收集信息；
* \-gccause 显示垃圾回收的相关信息（通-gcutil）,同时显示最后一次或当前正在发生的垃圾回收的诱因；
* \-printcompilation 输出JIT编译的方法信息；

### -class

显示加载class的数量，及所占空间等信息。

```bash
PWD:workspace pwd$ jstat -class 21668
Loaded  Bytes  Unloaded  Bytes     Time   
 10614 18813.4        1     0.9       4.60
```

* Loaded : 已经装载的类的数量
* Bytes : 装载类所占用的字节数
* Unloaded：已经卸载类的数量
* Bytes：卸载类的字节数
* Time：装载和卸载类所花费的时间

### -compiler

显示JIT编译的相关信息。

```bash
PWD:workspace pwd$ jstat -compiler 21668
Compiled Failed Invalid   Time   FailedType FailedMethod
    5461      0       0     1.67          0
```

* Compiled：编译任务执行数量
* Failed：编译任务执行失败数量
* Invalid ：编译任务执行失效数量
* Time ：编译任务消耗时间
* FailedType：最后一个编译失败任务的类型
* FailedMethod：最后一个编译失败任务所在的类及方法

### -gc

显示和gc相关的堆信息

```bash
PWD:workspace pwd$ jstat -gc 21668
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT   
18432.0 21504.0 18405.7  0.0   288768.0 70018.3   163840.0   23706.4   51200.0 48643.2 7168.0 6648.7     10    0.087   2      0.071    0.158
```

* S0C：年轻代中第一个survivor（幸存区）的容量 （字节）
* S1C：年轻代中第二个survivor（幸存区）的容量 (字节)
* S0U ：年轻代中第一个survivor（幸存区）目前已使用空间 (字节)
* S1U ：年轻代中第二个survivor（幸存区）目前已使用空间 (字节)
* EC ：年轻代中Eden（伊甸园）的容量 (字节)
* EU ：年轻代中Eden（伊甸园）目前已使用空间 (字节)
* OC ：Old代的容量 (字节)
* OU ：Old代目前已使用空间 (字节)
* MC：metaspace(元空间)的容量 (字节)
* MU：metaspace(元空间)目前已使用空间 (字节)
* CCSC:压缩类空间大小
* CCSU:压缩类空间使用大小
* YGC ：从应用程序启动到采样时年轻代中gc次数
* YGCT ：从应用程序启动到采样时年轻代中gc所用时间(s)
* FGC ：从应用程序启动到采样时old代(全gc)gc次数
* FGCT ：从应用程序启动到采样时old代(全gc)gc所用时间(s)
* GCT：从应用程序启动到采样时gc用的总时间(s)

### -gccapacity

显示各个代的容量以及使用情况，VM内存中三代（young,old,perm）对象的使用和占用大小

```bash
PWD:workspace pwd$ jstat -gccapacity 21668
 NGCMN    NGCMX     NGC     S0C   S1C       EC      OGCMN      OGCMX       OGC         OC       MCMN     MCMX      MC     CCSMN    CCSMX     CCSC    YGC    FGC 
 87040.0 1397760.0 344576.0 18432.0 21504.0 288768.0   175104.0  2796544.0   163840.0   163840.0      0.0 1093632.0  51200.0      0.0 1048576.0   7168.0     10     2
```

* NGCMN ：年轻代(young)中初始化(最小)的大小(字节)
* NGCMX ：年轻代(young)的最大容量 (字节)
* NGC ：年轻代(young)中当前的容量 (字节)
* S0C ：年轻代中第一个survivor（幸存区）的容量 (字节)
* S1C ： 年轻代中第二个survivor（幸存区）的容量 (字节)
* EC ：年轻代中Eden（伊甸园）的容量 (字节)
* OGCMN ：old代中初始化(最小)的大小 (字节)
* OGCMX ：old代的最大容量(字节)
* OGC：old代当前新生成的容量 (字节)
* OC ：Old代的容量 (字节)
* MCMN：metaspace(元空间)中初始化(最小)的大小 (字节)
* MCMX ：metaspace(元空间)的最大容量 (字节)
* MC ：metaspace(元空间)当前新生成的容量 (字节)
* CCSMN：最小压缩类空间大小
* CCSMX：最大压缩类空间大小
* CCSC：当前压缩类空间大小
* YGC ：从应用程序启动到采样时年轻代中gc次数
* FGC：从应用程序启动到采样时old代(全gc)gc次数

### -gcmetacapacity

显示metaspace的大小

```bash
PWD:workspace pwd$ jstat -gcmetacapacity 21668
   MCMN       MCMX        MC       CCSMN      CCSMX       CCSC     YGC   FGC    FGCT     GCT   
       0.0  1093632.0    51200.0        0.0  1048576.0     7168.0    10     2    0.071    0.158
```

* MCMN:最小元数据容量
* MCMX：最大元数据容量
* MC：当前元数据空间大小
* CCSMN：最小压缩类空间大小
* CCSMX：最大压缩类空间大小
* CCSC：当前压缩类空间大小
* YGC ：从应用程序启动到采样时年轻代中gc次数
* FGC ：从应用程序启动到采样时old代(全gc)gc次数
* FGCT ：从应用程序启动到采样时old代(全gc)gc所用时间(s)
* GCT：从应用程序启动到采样时gc用的总时间(s)

### -gcnew

显示新生代信息

```bash
PWD:workspace pwd$ jstat -gcnew 21668
 S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT  
18432.0 21504.0 18405.7    0.0  3  15 21504.0 288768.0  70018.3     10    0.087
```

* S0C ：年轻代中第一个survivor（幸存区）的容量 (字节)
* S1C ：年轻代中第二个survivor（幸存区）的容量 (字节)
* S0U ：年轻代中第一个survivor（幸存区）目前已使用空间 (字节)
* S1U ：年轻代中第二个survivor（幸存区）目前已使用空间 (字节)
* TT：持有次数限制
* MTT：最大持有次数限制
* DSS：期望的幸存区大小
* EC：年轻代中Eden（伊甸园）的容量 (字节)
* EU ：年轻代中Eden（伊甸园）目前已使用空间 (字节)
* YGC ：从应用程序启动到采样时年轻代中gc次数
* YGCT：从应用程序启动到采样时年轻代中gc所用时间(s)

### -gcnewcapacity

显示新生代大小和使用情况

```bash
PWD:workspace pwd$ jstat -gcnewcapacity 21668
  NGCMN      NGCMX       NGC      S0CMX     S0C     S1CMX     S1C       ECMX        EC      YGC   FGC 
   87040.0  1397760.0   344576.0 465920.0  18432.0 465920.0  21504.0  1396736.0   288768.0    10     2
```

* NGCMN ：年轻代(young)中初始化(最小)的大小(字节)
* NGCMX ：年轻代(young)的最大容量 (字节)
* NGC ：年轻代(young)中当前的容量 (字节)
* S0CMX ：年轻代中第一个survivor（幸存区）的最大容量 (字节)
* S0C ：年轻代中第一个survivor（幸存区）的容量 (字节)
* S1CMX ：年轻代中第二个survivor（幸存区）的最大容量 (字节)
* S1C：年轻代中第二个survivor（幸存区）的容量 (字节)
* ECMX：年轻代中Eden（伊甸园）的最大容量 (字节)
* EC：年轻代中Eden（伊甸园）的容量 (字节)
* YGC：从应用程序启动到采样时年轻代中gc次数
* FGC：从应用程序启动到采样时old代(全gc)gc次数

### -gcold

显示老年代和永久代的信息

```bash
PWD:workspace pwd$ jstat -gcold 21668
   MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT   
 51200.0  48643.2   7168.0   6648.7    163840.0     23706.4     10     2    0.071    0.158
```

* MC ：metaspace(元空间)的容量 (字节)
* MU：metaspace(元空间)目前已使用空间 (字节)
* CCSC:压缩类空间大小
* CCSU:压缩类空间使用大小
* OC：Old代的容量 (字节)
* OU：Old代目前已使用空间 (字节)
* YGC：从应用程序启动到采样时年轻代中gc次数
* FGC：从应用程序启动到采样时old代(全gc)gc次数
* FGCT：从应用程序启动到采样时old代(全gc)gc所用时间(s)
* GCT：从应用程序启动到采样时gc用的总时间(s)

### -gcoldcapacity

显示老年代的大小

```bash
PWD:workspace pwd$ jstat -gcoldcapacity 21668
   OGCMN       OGCMX        OGC         OC       YGC   FGC    FGCT     GCT   
   175104.0   2796544.0    163840.0    163840.0    10     2    0.071    0.158
```

* OGCMN ：old代中初始化(最小)的大小 (字节)
* OGCMX ：old代的最大容量(字节)
* OGC ：old代当前新生成的容量 (字节)
* OC ：Old代的容量 (字节)
* YGC ：从应用程序启动到采样时年轻代中gc次数
* FGC ：从应用程序启动到采样时old代(全gc)gc次数
* FGCT ：从应用程序启动到采样时old代(全gc)gc所用时间(s)
* GCT：从应用程序启动到采样时gc用的总时间(s)

### -gcutil

显示垃圾收集信息

```bash
PWD:workspace pwd$ jstat -gcutil 21668
  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT   
 99.86   0.00  24.25  14.47  95.01  92.76     10    0.087     2    0.071    0.158
```

* S0 ：年轻代中第一个survivor（幸存区）已使用的占当前容量百分比
* S1 ：年轻代中第二个survivor（幸存区）已使用的占当前容量百分比
* E ：年轻代中Eden（伊甸园）已使用的占当前容量百分比
* O ：old代已使用的占当前容量百分比
* P ：perm代已使用的占当前容量百分比
* YGC ：从应用程序启动到采样时年轻代中gc次数
* YGCT ：从应用程序启动到采样时年轻代中gc所用时间(s)
* FGC ：从应用程序启动到采样时old代(全gc)gc次数
* FGCT ：从应用程序启动到采样时old代(全gc)gc所用时间(s)
* GCT：从应用程序启动到采样时gc用的总时间(s)

### -gccause

显示垃圾回收的相关信息（通-gcutil）,同时显示最后一次或当前正在发生的垃圾回收的诱因

```bash
  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT    LGCC                 GCC                 
 99.86   0.00  24.25  14.47  95.01  92.76     10    0.087     2    0.071    0.158 Allocation Failure   No GC
```

* LGCC：最后一次GC原因
* GCC：当前GC原因（No GC 为当前没有执行GC）

### -printcompilation

输出JIT编译的方法信息

```bash
Compiled  Size  Type Method
    5466     41    1 java/text/DecimalFormat getPositiveSuffixFieldPositions
```

* Compiled ：编译任务的数目
* Size ：方法生成的字节码的大小
* Type：编译类型
* Method：类名和方法名用来标识编译的方法。类名使用/做为一个命名空间分隔符。方法名是给定类中的方法。上述格式是由-XX:+PrintComplation选项进行设置的

## jmap

命令jmap是一个多功能的命令。它可以生成 java 程序的 dump 文件， 也可以查看堆内对象示例的统计信息、查看 ClassLoader 的信息以及 finalizer 队列。

```bash
Usage:
    jmap [option] <pid>
        (to connect to running process)
    jmap [option] <executable <core>
        (to connect to a core file)
    jmap [option] [server_id@]<remote server IP or hostname>
        (to connect to remote debug server)

where <option> is one of:
    <none>               to print same info as Solaris pmap
    -heap                to print java heap summary
    -histo[:live]        to print histogram of java object heap; if the "live"
                         suboption is specified, only count live objects
    -clstats             to print class loader statistics
    -finalizerinfo       to print information on objects awaiting finalization
    -dump:<dump-options> to dump java heap in hprof binary format
                         dump-options:
                           live         dump only live objects; if not specified,
                                        all objects in the heap are dumped.
                           format=b     binary format
                           file=<file>  dump heap to <file>
                         Example: jmap -dump:live,format=b,file=heap.bin <pid>
    -F                   force. Use with -dump:<dump-options> <pid> or -histo
                         to force a heap dump or histogram when <pid> does not
                         respond. The "live" suboption is not supported
                         in this mode.
    -h | -help           to print this help message
    -J<flag>             to pass <flag> directly to the runtime system
```

option： 选项参数。

* pid： 需要打印配置信息的进程ID。
* executable： 产生核心dump的Java可执行文件。
* core： 需要打印配置信息的核心文件。
* server-id 可选的唯一id，如果相同的远程主机上运行了多台调试服务器，用此选项参数标识服务器。
* remote server IP or hostname 远程调试服务器的IP地址或主机名。

option

* no option： 查看进程的内存映像信息,类似 Solaris pmap 命令。
* heap： 显示Java堆详细信息
* histo\[:live]： 显示堆中对象的统计信息
* clstats：打印类加载器信息
* finalizerinfo： 显示在F-Queue队列等待Finalizer线程执行finalizer方法的对象
* dump:：生成堆转储快照
* F： 当-dump没有响应时，使用-dump或者-histo参数. 在这个模式下,live子参数无效.
* help：打印帮助信息
* J：指定传递给运行jmap的JVM的参数

### no option

命令：jmap pid

描述：查看进程的内存映像信息

使用不带选项参数的jmap打印共享对象映射，将会打印目标虚拟机中加载的每个共享对象的起始地址、映射大小以及共享对象文件的路径全称。

```bash
```

### heap

命令：jmap -heap pid

描述：显示Java堆详细信息

打印一个堆的摘要信息，包括使用的GC算法、堆配置信息和各内存区域内存使用信息

```bash
PWD:workspace pwd$ jmap -heap 21668
Attaching to process ID 21668, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.131-b11

using thread-local object allocation.
Parallel GC with 8 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 0
   MaxHeapFreeRatio         = 100
   MaxHeapSize              = 4294967296 (4096.0MB)
   NewSize                  = 89128960 (85.0MB)
   MaxNewSize               = 1431306240 (1365.0MB)
   OldSize                  = 179306496 (171.0MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
PS Young Generation
Eden Space:
   capacity = 295698432 (282.0MB)
   used     = 71698776 (68.37728118896484MB)
   free     = 223999656 (213.62271881103516MB)
   24.247262832966257% used
From Space:
   capacity = 18874368 (18.0MB)
   used     = 18847464 (17.974342346191406MB)
   free     = 26904 (0.02565765380859375MB)
   99.85745747884114% used
To Space:
   capacity = 22020096 (21.0MB)
   used     = 0 (0.0MB)
   free     = 22020096 (21.0MB)
   0.0% used
PS Old Generation
   capacity = 167772160 (160.0MB)
   used     = 24275368 (23.150794982910156MB)
   free     = 143496792 (136.84920501708984MB)
   14.469246864318848% used

26120 interned Strings occupying 2642096 bytes.
```

### histo\[:live]

命令：jmap -histo:live pid

描述：显示堆中对象的统计信息

其中包括每个Java类、对象数量、内存大小(单位：字节)、完全限定的类名。打印的虚拟机内部的类名称将会带有一个’\*’前缀。如果指定了live子选项，则只计算活动的对象。

```bash
PWD:workspace pwd$ jmap -histo:live 21668

 num     #instances         #bytes  class name
----------------------------------------------
   1:         56146        5781112  [C
   2:         16806        1478928  java.lang.reflect.Method
   3:         45538        1457216  java.util.concurrent.ConcurrentHashMap$Node
   4:         55985        1343640  java.lang.String
   5:         11212        1243680  java.lang.Class
   6:          2657        1159528  [B
   7:         22717         908680  java.util.LinkedHashMap$Entry
   8:         17788         853824  org.aspectj.weaver.reflect.ShadowMatchImpl
   9:         10326         743240  [Ljava.util.HashMap$Node;
  10:         13049         713896  [Ljava.lang.Object;
  11:         11087         620872  java.util.LinkedHashMap
  12:         17788         569216  org.aspectj.weaver.patterns.ExposedState
  13:           175         424592  [Ljava.util.concurrent.ConcurrentHashMap$Node;
  14:         12173         389536  java.util.HashMap$Node
  15:         17544         280704  java.lang.Object
  16:         10358         227056  [Ljava.lang.Class;
  17:          3490         156368  [I
  18:          6436         154464  java.util.ArrayList
  19:          1950         140400  org.springframework.core.annotation.AnnotationAttributes
```

### clstats

命令：jmap -clstats pid

描述：打印类加载器信息

\-clstats是-permstat的替代方案，在JDK8之前，-permstat用来打印类加载器的数据 打印Java堆内存的永久保存区域的类加载器的智能统计信息。对于每个类加载器而言，它的名称、活跃度、地址、父类加载器、它所加载的类的数量和大小都会被打印。此外，包含的字符串数量和大小也会被打印。

### finalizerinfo

命令：jmap -finalizerinfo pid

描述：打印等待终结的对象信息

```bash
PWD:workspace pwd$ jmap -finalizerinfo 21668
Attaching to process ID 21668, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.131-b11
Number of objects pending for finalization: 0
```

Number of objects pending for finalization: 0 说明当前F-QUEUE队列中并没有等待Fializer线程执行final

### dump:

命令：jmap -dump:format=b,file=heapdump.phrof pid

描述：生成堆转储快照dump文件。

以hprof二进制格式转储Java堆到指定filename的文件中。live子选项是可选的。如果指定了live子选项，堆中只有活动的对象会被转储。想要浏览heap dump，你可以使用jhat(Java堆分析工具)读取生成的文件。

这个命令执行，JVM会将整个heap的信息dump写入到一个文件，heap如果比较大的话，就会导致这个过程比较耗时，并且执行的过程中为了保证dump的信息是可靠的，所以会暂停应用， 线上系统慎用。

也可以设置内存溢出自动导出dump文件(内存很大的时候，可能会导不出来)

* \-XX:+HeapDumpOnOutOfMemoryError，当OutOfMemoryError发生时自动生成 Heap Dump 文件，这可是一个非常有用的参数，因为当你需要分析Java内存使用情况时，往往是在OOM(OutOfMemoryError)发生时。
* \-XX:+HeapDumpBeforeFullGC 当 JVM 执行 FullGC 前执行 dump。
* \-XX:+HeapDumpAfterFullGC当 JVM 执行 FullGC 后执行 dump。
* \-XX:+HeapDumpOnCtrlBreak交互式获取dump。在控制台按下快捷键Ctrl + Break时，JVM就会转存一下堆快照。
* \-XX:HeapDumpPath=./   （路径）

## jstack

jstack是jdk自带的线程堆栈分析工具，使用该命令可以查看或导出 Java 应用程序中线程堆栈信息。通过 jstack 命令可以获取当前进程的所有线程信息。每个线程堆中信息中，都可以查看到 线程ID、线程的状态（wait、sleep、running 等状态）、是否持有锁信息等

```bash
PWD:workspace pwd$ jstack -h
Usage:
    jstack [-l] <pid>
        (to connect to running process)
    jstack -F [-m] [-l] <pid>
        (to connect to a hung process)
    jstack [-m] [-l] <executable> <core>
        (to connect to a core file)
    jstack [-m] [-l] [server_id@]<remote server IP or hostname>
        (to connect to a remote debug server)

Options:
    -F  to force a thread dump. Use when jstack <pid> does not respond (process is hung)
    -m  to print both java and native frames (mixed mode)
    -l  long listing. Prints additional information about locks
    -h or -help to print this help message
```

参数说明：

* \-l 长列表. 打印关于锁的附加信息,例如属于java.util.concurrent 的 ownable synchronizers列表.
* \-F 当’jstack \[-l] pid’没有相应的时候强制打印栈信息
* \-m 打印java和native c/c++框架的所有栈信息.
* \-h | -help 打印帮助信息

pid 需要被打印配置信息的java进程id,可以用jps查询.

### jstack找出占用cpu最高的堆栈信息

1. 使用命令top -p  ，显示你的java进程的内存情况，pid是你的java进程号，比如4977
2. 按H，获取每个线程的内存情况 
3. 找到内存和cpu占用最高的线程tid，比如4977 
4. 转为十六进制得到 0x1371 ,此为线程id的十六进制表示
5. 执行 jstack 4977|grep -A 10 1371，得到线程堆栈信息中1371这个线程所在行的后面10行 
6. 查看对应的堆栈信息找出可能存在问题的代码
