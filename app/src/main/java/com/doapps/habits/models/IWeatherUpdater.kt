package com.doapps.habits.models

/**
 * An interface for entities handling weather information
 */
interface IWeatherUpdater {
  /**
   * Gets weather from service and updates an entity
   */
  fun getWeather()
}
