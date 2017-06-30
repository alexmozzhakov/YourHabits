package com.doapps.habits.models

@FunctionalInterface
interface IOnItemSelectedListener<T> {
    /**
     * Callback method to be invoked when an item has been selected.
     * @param item An item that is selected
     */
    fun onItemSelected(item: T)
}
