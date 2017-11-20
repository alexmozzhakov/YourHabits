package com.doapps.habits.models

interface IProgramCalculator {

  fun moneySaved(program: Program): Int

  fun timeSaved(program: Program): Int
}
