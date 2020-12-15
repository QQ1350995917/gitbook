package pwd.spring.boot.test;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * pwd.spring.boot.test@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-12-15 17:56
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class CustomerQueue<Runnable> extends ArrayBlockingQueue<Runnable> {

    public CustomerQueue(int capacity) {
        super(capacity);
    }

    public CustomerQueue(int capacity, boolean fair) {
        super(capacity, fair);
    }

    public CustomerQueue(int capacity, boolean fair,
        Collection<? extends Runnable> c) {
        super(capacity, fair, c);
    }
}
