package com.doapps.habits.models

import com.google.firebase.database.DataSnapshot

class FirebaseProgramView(private val snapshot: DataSnapshot) : IProgramViewProvider {
    private val title: String? = snapshot.child("name").getValue(String::class.java)
    private val percent: String? = "${snapshot.child("success").getValue(String::class.java)} SUCCESS"
    private val description: String? = snapshot.child("description").getValue(String::class.java)
    private val imageLink: String? = String.format("http://habit.esy.es/img_progs/%s.jpg",
            snapshot.child("image").getValue(String::class.java))

    override fun getTitle(): String? = title

    override fun getDescription(): String? = description

    override fun getPercent(): String? = percent

    override fun getImageLink(): String? = imageLink

    override fun getSnapshot(): DataSnapshot = snapshot
}