package com.doapps.habits.listeners

import java.util.*


class NameChangeListener : Observable() {
  private var mChanged = false

  public override fun setChanged() {
    mChanged = true
    super.notifyObservers()
  }

  override fun hasChanged(): Boolean {
    synchronized(this) {
      return mChanged
    }
  }

  companion object {
    val listener = NameChangeListener()
  }
}
