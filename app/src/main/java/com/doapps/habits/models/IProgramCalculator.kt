package com.doapps.habits.models

/**
 * An interface for handlers of any needed calculation of habits cost
 */
interface IProgramCalculator {

  fun moneySaved(program: Program): Int

  fun timeSaved(program: Program): Int
}
