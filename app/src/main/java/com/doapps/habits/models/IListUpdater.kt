package com.doapps.habits.models

interface IListUpdater<in T> {

  /**
   * Updates list with some data

   * @param data List, which will be updated on function call
   */
  fun updateList(data: List<T>)
}
