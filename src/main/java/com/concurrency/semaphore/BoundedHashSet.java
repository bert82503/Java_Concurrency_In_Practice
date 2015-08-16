package com.concurrency.semaphore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * 使用 Semaphore 为容器设置边界。
 *
 * <p>信号量的计数值会初始化为容器容量的最大值。
 * add操作在向底层容器中添加一个元素之前，首先要获取一个许可。如果add操作没有添加任何元素，那么会立刻释放许可。
 * 同样，remove操作释放一个许可，使更多的元素能够添加到容器中。
 * 底层的Set实现并不知道关于边界的任何信息，这是由 BoundedHashSet 来处理的。
 *
 * @author Bert Lee 2015年08月16日 19:46
 */
public class BoundedHashSet<T> {
    private final Set<T> set;
    private final Semaphore semaphore;

    public BoundedHashSet(int bound) {
        set = Collections.synchronizedSet(new HashSet<T>());
        semaphore = new Semaphore(bound);
    }

    public boolean add(T o) throws InterruptedException {
        semaphore.acquire();
        boolean wasAdded = false;
        try {
            wasAdded = set.add(o);
            return wasAdded;
        } finally {
            if (!wasAdded) {
                semaphore.release();
            }
        }
    }

    public boolean remove(Object o) {
        boolean wasRemoved = set.remove(o);
        if (wasRemoved) {
            semaphore.release();
        }
        return wasRemoved;
    }
}
