package com.doapps.habits.helper;

import java.util.Observable;


public class NameChangeListener extends Observable {
    private volatile boolean mChanged;
    public static final NameChangeListener listener = new NameChangeListener();

    public void setChanged() {
        mChanged = true;
        super.notifyObservers();
    }

    @Override
    public synchronized boolean hasChanged() {
        return mChanged;
    }
}
