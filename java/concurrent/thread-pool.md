## 八种线程池创建方式：
1. ExecutorService threadPool = Executors.newSingleThreadExecutor();//单线程的线程池，只有一个线程在工作
1. ExecutorService threadPool = Executors.newSingleThreadScheduledExecutor();//单线程的线程池，只有一个线程在工作
1. ExecutorService threadPool = Executors.newCachedThreadPool();//有缓冲的线程池，线程数 JVM 控制
1. ExecutorService threadPool = Executors.newFixedThreadPool(3);//固定大小的线程池
1. ExecutorService threadPool = Executors.newScheduledThreadPool(2);
1. ExecutorService threadPool = Executors.newWorkStealingPool(2);

## 两种底层实现
1.ExecutorService threadPool = new ThreadPoolExecutor();//默认线程池，可控制参数比较多
1.ExecutorService threadPool = new ForkJoinPool();//newWorkStealingPool的底层实现

## 三种阻塞队列：
1. BlockingQueue<Runnable> workQueue workQueue = new ArrayBlockingQueue<>(5);//基于数组的先进先出队列，有界
1. BlockingQueue<Runnable> workQueue workQueue = new LinkedBlockingQueue<>();//基于链表的先进先出队列，无界
1. BlockingQueue<Runnable> workQueue workQueue = new SynchronousQueue<>();//无缓冲的等待队列，无界

- SynchronousQueue
> SynchronousQueue没有容量，是无缓冲等待队列，是一个不存储元素的阻塞队列，会直接将任务交给消费者，必须等队列中的添加元素被消费后才能继续添加新的元素。
拥有公平（FIFO）和非公平(LIFO)策略，非公平侧罗会导致一些数据永远无法被消费的情况？
使用SynchronousQueue阻塞队列一般要求maximumPoolSizes为无界，避免线程拒绝执行操作。

- LinkedBlockingQueue
> LinkedBlockingQueue是一个无界缓存等待队列。当前执行的线程数量达到corePoolSize的数量时，剩余的元素会在阻塞队列里等待。（所以在使用此阻塞队列时maximumPoolSizes就相当于无效了），每个线程完全独立于其他线程。生产者和消费者使用独立的锁来控制数据的同步，即在高并发的情况下可以并行操作队列中的数据。
  
- ArrayBlockingQueue
> ArrayBlockingQueue是一个有界缓存等待队列，可以指定缓存队列的大小，当正在执行的线程数等于corePoolSize时，多余的元素缓存在ArrayBlockingQueue队列中等待有空闲的线程时继续执行，当ArrayBlockingQueue已满时，加入ArrayBlockingQueue失败，会开启新的线程去执行，当线程数已经达到最大的maximumPoolSizes时，再有新的元素尝试加入ArrayBlockingQueue时会报错。
   

## 四种拒绝策略：
1. RejectedExecutionHandler rejected = new ThreadPoolExecutor.AbortPolicy();//默认，队列满了丢任务抛出异常
1. RejectedExecutionHandler rejected = new ThreadPoolExecutor.DiscardPolicy();//队列满了丢任务不异常
1. RejectedExecutionHandler rejected = new ThreadPoolExecutor.DiscardOldestPolicy();//将最早进入队列的任务删，之后再尝试加入队列
1. RejectedExecutionHandler rejected = new ThreadPoolExecutor.CallerRunsPolicy();//如果添加到线程池失败，那么主线程会自己去执行该任务
   


## 同一个世界同一个梦想

java.util.concurrent.ThreadPoolExecutor.execute
java.util.concurrent.ForkJoinPool.execute
```java
public class ThreadPoolExecutor extends AbstractExecutorService {
    /**
     * Executes the given task sometime in the future.  The task
     * may execute in a new thread or in an existing pooled thread.
     *
     * If the task cannot be submitted for execution, either because this
     * executor has been shutdown or because its capacity has been reached,
     * the task is handled by the current {@code RejectedExecutionHandler}.
     *
     * @param command the task to execute
     * @throws RejectedExecutionException at discretion of
     *         {@code RejectedExecutionHandler}, if the task
     *         cannot be accepted for execution
     * @throws NullPointerException if {@code command} is null
     */
    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
        /*
         * Proceed in 3 steps:
         *
         * 1. If fewer than corePoolSize threads are running, try to
         * start a new thread with the given command as its first
         * task.  The call to addWorker atomically checks runState and
         * workerCount, and so prevents false alarms that would add
         * threads when it shouldn't, by returning false.
         *
         * 2. If a task can be successfully queued, then we still need
         * to double-check whether we should have added a thread
         * (because existing ones died since last checking) or that
         * the pool shut down since entry into this method. So we
         * recheck state and if necessary roll back the enqueuing if
         * stopped, or start a new thread if there are none.
         *
         * 3. If we cannot queue task, then we try to add a new
         * thread.  If it fails, we know we are shut down or saturated
         * and so reject the task.
         */
         
         
         /* 
          * execute 方法 分三种情况处理command对象 
          * 
          * 1. 如果当前活跃线程数小于corePoolSize，启动一个新的线程，并把当前task作为其第一个task。
          *    调用addWorker添加线程时，addWorker会自动检查runState和workerCount，addWork通过返回false而不是抛出异常确定是否添加线程
          *           
          * 
          * 2. 即时一个任务已经在排队了，依然需要通过双层检查确定是否要添加一个新的线程
          * 
          * 
          * 3. 队列应满了，不能容纳新的task，就通过拒绝策略反馈这个消息
          * 
          * 
          * 
          * 
          * 
          * 
          * 
          */
         
        int c = ctl.get();
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            if (! isRunning(recheck) && remove(command))
                reject(command);
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        else if (!addWorker(command, false))
            reject(command);
    }
}

```
         



