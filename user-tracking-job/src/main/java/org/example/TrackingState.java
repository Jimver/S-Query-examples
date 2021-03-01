package org.example;

import java.io.Serializable;

public class TrackingState implements Serializable {
    private final Counter clothesViews = new Counter();
    private final Counter foodViews = new Counter();
    private final Counter toolsViews = new Counter();
    private final Counter electronicsViews = new Counter();

    private Counter getCounter(Category category) {
        switch (category) {
            case CLOTHES:
                return clothesViews;
            case FOOD:
                return foodViews;
            case TOOLS:
                return toolsViews;
            case ELECTRONICS:
                return electronicsViews;
            default:
                throw new IllegalArgumentException("Invalid category: " + category.toString());
        }
    }

    public int getViews(Category category) {
        return getCounter(category).getValue();
    }

    public void incrementViews(Category category) {
        getCounter(category).increment();
    }

    public Category mostViews() {
        Category[] categories = Category.values();
        Category mostViews = categories[0];
        for (Category category: categories) {
            if (getCounter(category).getValue() > getCounter(mostViews).getValue()) {
                mostViews = category;
            }
        }
        return mostViews;
    }
}
