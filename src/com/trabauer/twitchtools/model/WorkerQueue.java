package com.trabauer.twitchtools.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Flo on 23.01.2015.
 */
public final class WorkerQueue<T extends Object> {
    private Vector<T> content;
    private int initialSize;

    public WorkerQueue(List<T> content) {
        this.content = new Vector<T>(content);
        this.initialSize = content.size();
    }

    public WorkerQueue() {
        this.content = new Vector<T>();
        this.initialSize = 0;
    }

    public T pop() {
        if(! content.isEmpty()) {
            T item = content.get(0);
            content.remove(item);
            return item;
        }
        return null;
    }

    public T peek() {
        if(! content.isEmpty()) {
            T item = content.get(0);
            return item;
        }
        return null;
    }

    public void append(T item ) {
        content.add(item);
        initialSize++;
    }

    public void append(List<T> items) {
        content.addAll(items);
        initialSize += items.size();
    }

    public void resetQueue(List<T> content) {
        this.content = new Vector<T>(content);
        this.initialSize = content.size();
    }

    public int size() {
        return content.size();
    }

    public boolean isEmpty() {
        return  content.isEmpty();
    }

    public int getInitialSize() {
        return initialSize;
    }
}
