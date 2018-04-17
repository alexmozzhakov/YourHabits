package com.doapps.habits.models

/**
 * An interface for network state listeners
 */
interface INetworkStateListener {
  /**
   * Handles a network state update
   */
  fun update()
}