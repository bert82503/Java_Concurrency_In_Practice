package com.concurrency.blockingqueue;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 示例：桌面搜索
 *
 * @author Bert Lee 2015年08月16日 17:37
 */
public class DesktopSearch {

    private static final int BOUND = 10000;
    private static final int CONSUMERS_MAX = 100;

    public static void startIndexing(File[] roots) {
        BlockingQueue<File> queue = new LinkedBlockingQueue<>(BOUND);
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return true;
            }
        };

        for (File root : roots) {
            new Thread(new FileCrawler(queue, filter, root)).start();
        }
        for (int i = 0; i < CONSUMERS_MAX; i++) {
            new Thread(new Indexer(queue)).start();
        }
    }

}
