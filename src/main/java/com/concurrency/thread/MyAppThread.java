package com.concurrency.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 定制 Thread 基类，包括 为线程指定名字，设置自定义 UncaughtExceptionHandler 向 Logger 中写入信息，
 * 维护一些统计信息（包括有多少个线程被创建和销毁），以及在线程被创建或者终止时把调试消息写入日志。
 *
 * @author xingle
 * @since 2016年06月25日 22:12
 */
public class MyAppThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(MyAppThread.class);


    private static final String DEFAULT_NAME = "MyAppThread";

    private static volatile boolean debugLifecycle = false;

    private static final AtomicInteger created = new AtomicInteger(0);

    private static final AtomicInteger alive = new AtomicInteger(0);

    public MyAppThread(Runnable runnable) {
        this(runnable, DEFAULT_NAME);
    }

    public MyAppThread(Runnable runnable, String poolName) {
        super(runnable, poolName + "-" + created.incrementAndGet());
        setUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        logger.error("Uncaught in thread {}", t.getName(), e);
                    }
                }
        );
    }

    @Override
    public void run() {
        // 复制 debug 标志以确保一致的值
        boolean debug = debugLifecycle;
        if (debug) {
            logger.info("Created '{}' thread", getName());
        }

        try {
            alive.incrementAndGet();
            super.run();
        } finally {
            alive.decrementAndGet();

            if (debug) {
                logger.info("Exiting '{}' thread", getName());
            }
        }
    }

    public static int getThreadsCreated() {
        return created.get();
    }

    public static int getThreadsAlive() {
        return alive.get();
    }

    public static boolean isDebug() {
        return debugLifecycle;
    }

    public static void setDebug(boolean debug) {
        debugLifecycle = debug;
    }
}
