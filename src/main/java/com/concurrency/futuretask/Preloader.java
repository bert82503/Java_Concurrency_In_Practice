package com.concurrency.futuretask;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 使用 FutureTask 来提前加载稍后需要的数据。
 * <p>
 *     Preloader 使用 FutureTask 来执行一个高开销的计算，并且计算结果将在稍后使用。
 *     通过提前启动计算，可以减少在等待结果时需要的时间。
 * </p>
 *
 * @author Bert Lee 2015年08月16日 18:28
 */
public class Preloader {
    private final FutureTask<ProductInfo> futureTask =
            new FutureTask<ProductInfo>(new Callable<ProductInfo>() {
                @Override
                public ProductInfo call() throws DataLoadException {
                    return loadProductInfo();
                }

                private ProductInfo loadProductInfo() {
                    // FIXME 根据自己的业务需求实现
                    return new ProductInfo();
                }
            }
    );
    private final Thread thread = new Thread(futureTask);

    public void start() {
        thread.start();
    }

    public ProductInfo get()
            throws DataLoadException, InterruptedException {
        try {
            return futureTask.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof DataLoadException) {
                throw (DataLoadException) cause;
            } else {
                throw launderThrowable(cause);
            }
        }
    }

    /**
     * 转换为业务自定义的异常类。
     *
     * @param cause
     * @return
     */
    private DataLoadException launderThrowable(Throwable cause) {
        return new DataLoadException(cause.getMessage(), cause.getCause());
    }
}
