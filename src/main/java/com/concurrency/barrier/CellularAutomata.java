package com.concurrency.barrier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 使用 CyclicBarrier 协调细胞自动衍生系统中的计算。
 *
 * @author Bert Lee 2015年08月16日 20:30
 */
public class CellularAutomata {
    private final Board mainBoard;
    private final CyclicBarrier barrier;
    private final List<Worker> workers;

    private CellularAutomata(Board board) {
        this.mainBoard = board;
        int count = Runtime.getRuntime().availableProcessors();
        this.barrier = new CyclicBarrier(count,
                new Runnable() {
                    @Override
                    public void run() {
                        mainBoard.commitNewValues();
                    }
                });
        this.workers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            workers.add(new Worker(mainBoard.getSubBoard(count, i)));
        }
    }

    private class Worker implements Runnable {
        private final Board board;

        public Worker(Board board) {
            this.board = board;
        }

        @Override
        public void run() {
            while (!board.hasConverged()) {
                for (int x = 0, maxX = board.getMaxX(); x < maxX; x++) {
                    for (int y = 0, maxY = board.getMaxY(); y < maxY; y++) {
                        board.setNewValue(x, y, computeValue(x, y));
                    }
                }
                try {
                    barrier.await(); // 一直阻塞等待，直到中断发生
//                    barrier.await(50, TimeUnit.MILLISECONDS); // 带超时的等待
                } catch (InterruptedException e) {
                    return;
                } catch (BrokenBarrierException e) {
                    return;
                }
//                catch (TimeoutException e) {
//                    return;
//                }
            }
        }

        private int computeValue(int x, int y) {
            return x + y;
        }
    }
    
    public void start() {
        for (int i = 0, size = workers.size(); i < size; i++) {
            new Thread(workers.get(i)).start();
        }
        mainBoard.waitForConvergence();
    }

}
