package org.example.state;

public class OrderState {
    private long size;
    private long total;

    public OrderState() {
        this.size = 0;
        this.total = 0;
    }

    public OrderState(long size, long total) {
        this.size = size;
        this.total = total;
    }

    public long getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }

    public void incrementSize() {
        size++;
    }

    public void decrementSize() {
        if (size > 0) {
            size--;
        }
    }

    public void deltaTotal(long delta) {
        if (total + delta > 0) {
            total += delta;
        }
    }
}
