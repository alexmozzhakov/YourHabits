package com.doapps.habits.listeners;

import java.util.Observable;


public class NameChangeListener extends Observable {
    private boolean mChanged;
    public static final NameChangeListener listener = new NameChangeListener();

    public void setChanged() {
        mChanged = true;
        super.notifyObservers();
    }

    @Override
    public boolean hasChanged() {
        synchronized (this) {
            return mChanged;
        }
    }
}
