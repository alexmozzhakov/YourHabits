package com.doapps.habits.models

import com.google.firebase.database.DataSnapshot

/**
 * An interface for providers of program view
 */
interface IProgramViewProvider {

  /**
   * Returns title of Program
   *
   * @return Title of [Program]
   */
  val title: String?

  /**
   * @return Description of [Program]
   */
  val description: String?

  /**
   * @return Success rate of [Program]
   */
  val percent: String?

  /**
   * @return Link to image of [Program]
   */
  val imageLink: String?

  /**
   * @return Data snapshot of [Program]
   */
  val snapshot: DataSnapshot
}
