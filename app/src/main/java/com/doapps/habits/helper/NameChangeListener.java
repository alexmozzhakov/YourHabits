package com.doapps.habits.helper;

import java.util.Observable;


public class NameChangeListener extends Observable {
    private volatile boolean changed;
    public static final NameChangeListener listener = new NameChangeListener();

    public void setChanged(final boolean changed) {
        this.changed = changed;
        super.notifyObservers();
    }

    @Override
    public synchronized boolean hasChanged() {
        return changed;
    }
}
