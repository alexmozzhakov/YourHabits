package com.doapps.habits.helper

import com.google.firebase.database.DataSnapshot

interface ClickableDataSnapshotFragment {
  fun onClick(position: Int, dataSnapshot: DataSnapshot)
}
