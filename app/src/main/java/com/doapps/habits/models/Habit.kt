package com.doapps.habits.models

import java.util.*

class Habit_(
        var id: Int,
        val title: String, private val question: String, private var doneMarker: Boolean,
        markerUpdatedDay: Int, markerUpdatedMonth: Int, markerUpdatedYear: Int = 0,
        private val time: Int, private val daysFollowing: Int, private val cost: Int,
        vararg frequencyArray: Int) {

    val frequencyArray: IntArray
    var markerUpdatedDay: Int = 0
        private set
    var markerUpdatedMonth: Int = 0
        private set
    var markerUpdatedYear: Int = 0
        private set

    init {
        this.markerUpdatedDay = markerUpdatedDay
        this.markerUpdatedMonth = markerUpdatedMonth
        this.markerUpdatedYear = markerUpdatedYear
        this.frequencyArray = frequencyArray
    }

    fun setDoneMarker(doneMarker: Boolean) {
        this.doneMarker = doneMarker
        if (doneMarker) {
            val calendar = Calendar.getInstance()
            markerUpdatedDay = calendar.get(Calendar.DATE)
            markerUpdatedMonth = calendar.get(Calendar.MONTH)
            markerUpdatedYear = calendar.get(Calendar.YEAR)
        }
    }

    fun isDone(markerUpdatedDay: Int, markerUpdatedMonth: Int, markerUpdatedYear: Int): Boolean {
        return markerUpdatedDay == this.markerUpdatedDay &&
                markerUpdatedMonth == this.markerUpdatedMonth &&
                markerUpdatedYear == this.markerUpdatedYear &&
                doneMarker
    }

}
