# Java并发—同步工具类

## CountDownLatch 同步倒数计数器

CountDownLatch是一个同步倒数计数器。CountDownLatch允许一个或多个线程等待其他线程完成操作。

CountDownLatch对象内部存有一个整数作为计数器。调用countDown()方法就将计数器减1，当计数到达0时，则所有等待者会停止等待。计数器的操作是原子性的。

CountDownLatch类的常用API

构造方法  

*CountDownLatch(int count) * 构造方法参数指定了计数的次数。

方法
- void await() 使当前线程在锁存器倒计数至0之前一直等待，除非线程被中断。
- boolean await(long timeout, TimeUnit unit) 使当前线程在锁存器倒计数至0之前一直等待，除非线程被中断或超出了指定的等待时间。
- void countDown() 计数减1。当计数为0，则释放所有等待的线程。
- long getCount() 返回当前计数。
- String toString() 返回标识此锁存器及其状态的字符串。
用给定的计数初始化 CountDownLatch实例。每调用一次countDown()方法，计数器减1。计数器大于0 时，await()方法会阻塞其他线程继续执行。 利用该特性，可以让主线程等待子线程的结束。

需要注意的是，一旦CountDownLatch的计数到0，则无法再将该计数无法被重置。

一种典型的场景就是火箭发射。在火箭发射前，为了保证万无一失，往往还要进行各项设备、仪器的检查。只有等所有检查完毕后，引擎才能点火。这种场景就非常适合使用CountDownLatch。它可以使得点火线程，等待所有检查线程全部完工后，再执行。
例：有三个工人在为老板干活。老板有一个习惯，当三个工人把一天的活都干完了的时候，他就来检查所有工人所干的活。如下代码设计两个类，Worker代表工人，Boss代表老板。

```
import java.util.Random; import java.util.concurrent.CountDownLatch; 
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; 
import java.util.concurrent.TimeUnit; 
public class CountDownLatchDemo { 
public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(3);    // 同步倒数计数器。
 Worker w1 = new Worker(latch, "张三");
        Worker w2 = new Worker(latch, "李四");
        Worker w3 = new Worker(latch, "王五");
        Boss boss = new Boss(latch);

        executor.execute(w3); // 工人工作。
 executor.execute(w2);
        executor.execute(w1);
        executor.execute(boss); // 老板工作。
 executor.shutdown();
    }
}
 class Worker implements Runnable { 
private CountDownLatch downLatch; 
private String name; 
public Worker(CountDownLatch downLatch, String name) { 
this.downLatch = downLatch; 
this.name = name;
 } 
public void run() { 
        this.doWork();    // 工人工作。
        try {
            TimeUnit.SECONDS.sleep(new Random().nextInt(10));  // 工作时长。
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(this.name + "活干完了！"); 
        this.downLatch.countDown();  // 计数减1。
 } private void doWork() {
        System.out.println(this.name + "正在干活!");
    }
} 
class Boss implements Runnable { 
private CountDownLatch downLatch; 
public Boss(CountDownLatch downLatch) { 
this.downLatch = downLatch;
    } public void run() {
        System.out.println("老板正在等所有的工人干完活......"); try { this.downLatch.await();    // 当计数不为0时，线程永远阻塞。为0则继续执行。
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("工人活都干完了，老板开始检查了！");
    }
}
```

CountDownLatch类与join方法

CountDownLatch实例本质与Thread的join方法相同。但join方法仅可以支持当前线程等待一个线程的结束，若需要等待多个线程，则需要逐个线程的调用join方法，非常麻烦。CountDwonLatch可以很方便的实现一个线程等待多个线程。


## CyclicBarrier 循环屏障
CyclicBarrier用于让一组线程运行并互相等待，直到共同到达一个公共屏障点 (common barrier point，又被称为同步点)，被屏障拦截的所有线程就会继续执行。

CyclicBarrier与CountDownLatch的功能非常类似。但一个CyclicBarrier实例在释放等待线程后可以继续使用。让下一批线程在屏障点等待。但CountDownLatch实例只能被使用一次。所以CyclicBarrier被称为*循环 *的 barrier。

典型的比如公司的人员利用集体郊游，先各自从家出发到公司集合，再同时出发游玩，在指定地点集合。CyclicBarrier表示大家彼此在某处等待，集合好后才开始出发，分散活动后又在指定地点集合碰面。

CyclicBarrier类API

构造器

- CyclicBarrier(int parties) 创建CyclicBarrier对象，parties 表示屏障拦截的线程数量。

- CyclicBarrier(int parties, Runnable barrierAction) 创建 CyclicBarrier对象，该构造方法提供了一个Runnable 参数，在一组线程中的最后一个线程到达之后，执行Runnable中的程序，再之后释放正在等待的线程。Runnable在屏障点上只运行一次。

方法

- int await() 通知CyclicBarrier实例，当前线程已经到达屏障点，然后当前线程将被阻塞。
- 
- int await(long timeout, TimeUnit unit) 指定当前线程被阻塞的时间。
- 
- int getNumberWaiting() 返回当前在屏障处等待的线程数。
- 
- int getParties() 返回CyclicBarrier的需要拦截的线程数。
- 
- boolean isBroken() 查询此屏障是否处于损坏状态。
- 
- void reset() 将屏障重置为其初始状态。

例1：各省数据独立，分库存偖。为了提高计算性能，统计时采用每个省开一个线程先计算单省结果，最后汇总。
```
import java.util.concurrent.BrokenBarrierException; 
import java.util.concurrent.CyclicBarrier; 
public class Total { 
public static void main(String[] args) {
        TotalService totalService = new TotalServiceImpl();
        CyclicBarrier barrier = new CyclicBarrier(5, new TotalTask(totalService)); // 实际系统是查出所有省编码code的列表，然后循环，每个code生成一个线程。
        new BillTask(new BillServiceImpl(), barrier, "北京").start(); 
        new BillTask(new BillServiceImpl(), barrier, "上海").start(); 
        new BillTask(new BillServiceImpl(), barrier, "广西").start(); 
        new BillTask(new BillServiceImpl(), barrier, "四川").start(); 
        new BillTask(new BillServiceImpl(), barrier, "黑龙江").start();
    }

} /** * 主任务：汇总任务 */
class TotalTask implements Runnable { 
    private TotalService totalService;

    TotalTask(TotalService totalService) { 
    this.totalService = totalService;
    } public void run() { 
    // 读取内存中各省的数据汇总，过程略。
     totalService.count();
        System.out.println("开始全国汇总");
    }

} /** * 子任务：计费任务 */
class BillTask extends Thread { 
    private BillService billService;     // 计费服务
    private CyclicBarrier barrier; 
    private String code;    // 代码，按省代码分类，各省数据库独立。
BillTask(BillService billService, CyclicBarrier barrier, String code) { 
    this.billService = billService; 
    this.barrier = barrier; 
    this.code = code;
    } 
public void run() {
        System.out.println("开始计算--" + code + "省--数据！");
        billService.bill(code); // 把bill方法结果存入内存，如ConcurrentHashMap,vector等,代码略
        System.out.println(code + "省已经计算完成,并通知汇总Service！"); 
        try { // 通知barrier已经完成
        barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
} 
interface BillService { 
      public void bill(String code);
} 
interface TotalService { 
      public void count();
} 
class BillServiceImpl implements BillService{

    @Override 
    public void bill(String code) {}
} 
class TotalServiceImpl implements TotalService{

    @Override 
    public void count(){}
}
```
例2：赛跑时，等待所有人都准备好时，才起跑。
```
public class CyclicBarrierTest { public static void main(String[] args) throws IOException, InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(3);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(new Thread(new Runner(barrier, "1号选手")));
        executor.submit(new Thread(new Runner(barrier, "2号选手")));
        executor.submit(new Thread(new Runner(barrier, "3号选手")));

        executor.shutdown();
    }
} class Runner implements Runnable { // 一个同步辅助类，它允许一组线程互相等待，直到到达某个公共屏障点 (common barrier point)
    private CyclicBarrier barrier; private String name; 
    public Runner(CyclicBarrier barrier, String name) { 
    super(); 
    this.barrier = barrier; 
    this.name = name;
    }

    @Override 
    public void run() { 
    try {
            Thread.sleep(1000 * (new Random()).nextInt(8));
            System.out.println(name + " 准备好了..."); // barrier的await方法，在所有参与者都已经在此           barrier 上调用 await 方法之前，将一直等待。
       barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println(name + " 起跑！");
    }

}
```
## Semaphore 信号量
Semaphore用于控制并发线程数。Semaphore实例可以控制当前访问自身的线程个数。使用Semaphore可以控制同时访问资源的线程个数。例如，实现一个文件允许的并发访问数。

Semaphore维护了一个许可集。“许可”即线程进入临界区的许可。一个临界区可以有多个许可。获取许可的线程即可进入。通过 acquire() 获取一个许可，如果线程没有获取到就等待，而 release() 表示释放一个许可。可以把Semaphore看成是一种共享锁。Semaphore允许同一时间多个线程同时访问临界区。

生活的理解：Semaphore实现的功能就类似厕所有5个坑，假如有十个人要上厕所，那么同时能有多少个人去上厕所呢？同时只能有5个人能够占用，当5个人中的任何一个人让开后，其中在等待的另外5个人中又有一个可以占用了。另外等待的5个人中可以是随机获得优先机会，也可以是按照先来后到的顺序获得机会，这取决于构造Semaphore对象时传入的参数选项。

Semaphore对象也可以实现互斥锁的功能，并且可以是由一个线程获得了"锁"，再由另一个线程释放"锁"，这可应用于死锁恢复的一些场合。

在一些企业系统中，开发人员经常需要限制未处理的特定资源请求（线程/操作）数量，事实上，限制有时候能够提高系统的吞吐量，因为它们减少了对特定资源的争用。尽管完全可以手动编写限制代码，但使用 Semaphore类可以更轻松地完成此任务，它将帮您执行限制。

常用API

- public void acquire() // 获取许可。
- public void acquireUninterruptibly()
- public boolean tryAcquire()
- public boolean tryAcquire(long timeout, TimeUnit unit)
- public void release() // 释放许可。该方法一般调用于finally块中。

例：10 个线程都在运行，可以对运行SemaphoreApp的Java进程执行jstack来验证，只有3个线程是活跃的。在一个信号计数器释放之前，其他7个线程都处于空闲状态。
```
import java.util.Random;
import java.util.concurrent.Semaphore; 
public class SemaphoreApp { 
public static void main(String[] args) { // 匿名Runnable实例。定义线程运行程序。
        Runnable limitedCall = new Runnable() { final Random rand = new Random(); final Semaphore available = new Semaphore(3);     // 最多可以发出3个"许可"
            int count = 0; public void run() { int time = rand.nextInt(15); int num = count++; try {
                    available.acquire(); // 当前线程获取"许可"。若没有获取许可，则等待于此。
                    System.out.println("Executing " + "long-running action for " + time + " seconds... #" + num); 
                    Thread.sleep(time * 1000); 
                    System.out.println("Done with #" + num + "!"); 
                } catch (InterruptedException intEx) { 
                    intEx.printStackTrace(); 
                } finally {
                    available.release(); // 当前线程释放"许可"
 }
            } 
        }; for (int i = 0; i < 10; i++) { new Thread(limitedCall).start(); 
        }
}
```
## Exchanger  交换器

Exchanger用于实现线程间的数据交换。Exchanger提供一个同步点，在同步点上，两个线程使用exchange方法交换彼此数据。如果第一个线程先执行exchange方法，则它会等待第二个线程执行exchange方法。当两个线程同时到达同步点时，这两个线程即可以交换数据。交换完毕后，各自进行以后的程序流程。当两个线程通过Exchanger交换数据的时候，这个交换对于两个线程来说是线程安全的。

exchange()方法将本线程的数据作为参数，传递给伙伴线程，并且该方法返回伙伴线程提供的数据。

当在运行不对称的活动时Exchanger很有用，比如当一个线程填充了buffer，另一个线程从buffer中消费数据时，这两个线程可以用Exchanger来交换数据。

Exchanger<V>类的API

构造器

Exchanger() 创建一个新的 Exchanger。

方法

- V exchange(V x) 等待另一个线程到达此交换点（除非当前线程被中断），然后将给定的对象传送给该线程，并接收该线程的对象。
 
- V exchange(V x, long timeout, TimeUnit unit) 等待另一个线程到达此交换点（除非当前线程被中断，或者超出了指定的等待时间），然后将给定的对象传送给该线程，同时接收该线程的对象。

例：以下这个程序demo要做的事情就是生产者在交换前生产5个"生产者"，然后再与消费者交换5个数据，然后再生产5个"交换后生产者"，而消费者要在交换前消费5个"消费者"，然后再与生产者交换5个数据，然后再消费5个"交换后消费者"。importjava.util.ArrayList;

```
import java.util.Iterator; 
import java.util.List; 
import java.util.concurrent.Exchanger;
 /** * 两个线程间的数据交换 */
public class ExchangerDemo { 
private static final Exchanger<List<String>> ex = new Exchanger<List<String>>(); 
private static void sleep(long millis){ 
  try {
          Thread.sleep(millis);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
}
     
     /** * 内部类，数据生成者 */
    class DataProducer implements Runnable { 
    private List<String> list = new ArrayList<String>();
    public void run() {
            System.out.println("生产者开始生产数据"); for (int i = 1; i <= 5; i++) {
                System.out.println("生产了第" + i + "个数据，耗时1秒");
                list.add("生产者" + i);
                sleep(1000);
            }
            System.out.println("生产数据结束");
            System.out.println("开始与消费者交换数据"); try { //将数据准备用于交换，并返回消费者的数据
                list = (List<String>) ex.exchange(list);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("结束与消费者交换数据");
            System.out.println("生产者与消费者交换数据后，再生产数据"); for (int i = 6; i < 10; i++) {
                System.out.println("交换后生产了第" + i + "个数据，耗时1秒");
                list.add("交换后生产者" + i);
                sleep(1000);
            }

            System.out.println("遍历生产者交换后的数据"); for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
                System.out.println(iterator.next());
            }
        }

    } /** * 内部类，数据消费者 */
    class DataConsumer implements Runnable { 
    private List<String> list = new ArrayList<String>(); 
    public void run() {
            System.out.println("消费者开始消费数据"); for (int i = 1; i <= 5; i++) {
                System.out.println("消费了第" + i + "个数据"); // 消费者产生数据，后面交换的时候给生产者
                list.add("消费者" + i);
             }

            System.out.println("消费数据结束");
            System.out.println("开始与生产者交换数据"); try { // 进行数据交换，返回生产者的数据
                list = (List<String>) ex.exchange(list);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("消费者与生产者交换数据后，再消费数据"); for (int i = 6; i < 10; i++) {
                System.out.println("交换后消费了第" + i + "个数据");
                list.add("交换后消费者" + i);
                sleep(1000);
            }
            sleep(1000);
            System.out.println("开始遍历消费者交换后的数据"); for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
                System.out.println(iterator.next());
            }
        }
    } // 主方法
    public static void main(String args[]) {
        ExchangerDemo et = new ExchangerDemo(); new Thread(et.new DataProducer()).start(); new Thread(et.new DataConsumer()).start();
    }
}
```


## 参考
https://www.jianshu.com/p/e65382d5fbda
