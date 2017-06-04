package com.doapps.habits.listeners;

import java.util.Observable;


public class EmptyListListener extends Observable {
    public static final EmptyListListener listener = new EmptyListListener();
    private boolean mChanged;

    public void isEmpty(boolean status) {
        mChanged = status;
        if (mChanged) {
            super.notifyObservers();
        }
    }

    @Override
    public synchronized boolean hasChanged() {
        return mChanged;
    }

}