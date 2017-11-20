package com.doapps.habits.models

import com.google.firebase.database.DataSnapshot

interface IProgramViewProvider {

  /**
   * Returns title of Program
   *
   * @return Title of [com.doapps.habits.models.Program]
   */
  val title: String?

  /**
   * @return Description of [com.doapps.habits.models.Program]
   */
  val description: String?

  /**
   * @return Success rate of [com.doapps.habits.models.Program]
   */
  val percent: String?

  /**
   * @return Link to image of [com.doapps.habits.models.Program]
   */
  val imageLink: String?

  /**
   * @return Data snapshot of [com.doapps.habits.models.Program]
   */
  val snapshot: DataSnapshot
}
