package com.doapps.habits.listeners

import java.util.*


class EmptyListListener : Observable() {
  private var mChanged = false

  fun isEmpty(status: Boolean) {
    mChanged = status
    if (mChanged) {
      super.notifyObservers()
    }
  }

  override fun hasChanged(): Boolean {
    synchronized(this) {
      return mChanged
    }
  }

  companion object {
    val listener = EmptyListListener()
  }
}