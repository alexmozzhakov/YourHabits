package com.doapps.habits.helper

import com.google.firebase.database.DataSnapshot

/**
 * An interface for click listeners for lists binded to
 */
interface ClickableDataSnapshotList {
  fun onClick(position: Int, dataSnapshot: DataSnapshot)
}
