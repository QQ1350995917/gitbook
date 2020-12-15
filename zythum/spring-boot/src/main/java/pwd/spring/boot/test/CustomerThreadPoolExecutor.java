package pwd.spring.boot.test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * pwd.spring.boot.test@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-12-15 17:58
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class CustomerThreadPoolExecutor extends ThreadPoolExecutor {

    public CustomerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
        TimeUnit unit,
        BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public CustomerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
        TimeUnit unit,
        BlockingQueue<Runnable> workQueue,
        ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public CustomerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
        TimeUnit unit,
        BlockingQueue<Runnable> workQueue,
        RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public CustomerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
        TimeUnit unit,
        BlockingQueue<Runnable> workQueue,
        ThreadFactory threadFactory,
        RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,
            handler);
    }
}
