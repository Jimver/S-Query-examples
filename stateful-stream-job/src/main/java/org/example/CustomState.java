package org.example;

import java.io.Serializable;

public class CustomState implements Serializable {
    private Long longState = 0L;

    public Long getLongState() {
        return longState;
    }

    public void setLongState(Long longState) {
        this.longState = longState;
    }
}
