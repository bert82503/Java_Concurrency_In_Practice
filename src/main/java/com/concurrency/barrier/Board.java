package com.concurrency.barrier;

/**
 * 类Board的实现描述：TODO 类实现描述
 *
 * @author Bert Lee 2015年08月16日 20:31
 */
public class Board {
    private static final int MAX_X = 100;
    private static final int MAX_Y = 100;

    private int x;
    private int y;
    private int z;

    private Board(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setNewValue(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Board getSubBoard(int count, int i) {
        return new Board(count, i);
    }

    public boolean hasConverged() {
        if (x >= 0 && x <= MAX_X && y >= 0 && y <= MAX_Y) {
            return true;
        }
        return false;
    }

    public void waitForConvergence() {
        // FIXME 自定义实现
    }

    public void commitNewValues() {
        // FIXME 提交新的值
    }

    public int getMaxX() {
        return MAX_X;
    }

    public int getMaxY() {
        return MAX_Y;
    }

}
