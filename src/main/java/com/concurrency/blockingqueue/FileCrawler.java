package com.concurrency.blockingqueue;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

/**
 * 文件爬虫者，生产者任务，即在某个文件层次结构中搜索符合索引标准的文件，
 * 并将它们的名称放入工作队列。
 *
 * @author Bert Lee 2015年08月16日 17:05
 */
public class FileCrawler implements Runnable {
    private final BlockingQueue<File> fileBlockingQueue;
    private final FileFilter fileFilter;
    private final File root;

    public FileCrawler(BlockingQueue<File> fileBlockingQueue, FileFilter fileFilter, File root) {
        this.fileBlockingQueue = fileBlockingQueue;
        this.fileFilter = fileFilter;
        this.root = root;
    }

    @Override
    public void run() {
        try {
            crawl(root);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void crawl(File root) throws InterruptedException {
        File[] entries = root.listFiles(fileFilter);
        if (entries != null) {
            for (File entry : entries) {
                if (entry.isDirectory()) {
                    crawl(entry);
                } else if (!alreadyIndexed(entry)) {
                    fileBlockingQueue.put(entry);
                }
            }
        }
    }

    private boolean alreadyIndexed(File entry) {
        // FIXME 未使用全局的已索引文件名称集合（ConcurrentSkipListSet）作为判断源，可能会存在相同文件被重复建索引
        return fileBlockingQueue.contains(entry);
    }

}
