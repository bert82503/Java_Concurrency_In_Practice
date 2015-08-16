package com.concurrency.futuretask;

/**
 * 数据加载异常表示。
 *
 * @author Bert Lee 2015年08月16日 18:46
 */
public class DataLoadException extends Exception {

    public DataLoadException(String message) {
        super(message);
    }

    public DataLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    private DataLoadException(Throwable cause) {
        super(cause);
    }

    private DataLoadException() {
        super();
    }

}
