package com.doapps.habits.models

import com.google.firebase.database.DataSnapshot

class FirebaseProgramView(override val snapshot: DataSnapshot) : IProgramViewProvider {
  override val title: String? =
      snapshot.child("name").getValue(String::class.java)

  override val percent: String? =
      "${snapshot.child("success").getValue(String::class.java)} SUCCESS"

  override val description: String? =
      snapshot.child("description").getValue(String::class.java)

  override val imageLink: String? = "http://habit.esy.es/img_progs/" +
      "${snapshot.child("image").getValue(String::class.java)}.jpg"
}