package com.doapps.habits.adapter

interface IMovableListAdapter {

    /* Called when item is moved from fromPosition to toPosition */
    fun onItemMove(fromPosition: Int, toPosition: Int)

    /* Called when item is dismissed */
    fun onItemDismiss(position: Int)
}