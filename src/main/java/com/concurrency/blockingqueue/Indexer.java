package com.concurrency.blockingqueue;

import java.io.File;
import java.util.concurrent.BlockingQueue;

/**
 * 文件索引者，消费者任务，即从队列中取出文件名称并对它们建立索引。
 *
 * @author Bert Lee 2015年08月16日 17:21
 */
public class Indexer implements Runnable {
    private final BlockingQueue<File> fileBlockingQueue;

    public Indexer(BlockingQueue<File> fileBlockingQueue) {
        this.fileBlockingQueue = fileBlockingQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                indexFile(fileBlockingQueue.take());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void indexFile(File file) {
        // FIXME “索引文件”实现逻辑
    }
}
