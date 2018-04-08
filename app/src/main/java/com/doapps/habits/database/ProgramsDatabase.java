package com.doapps.habits.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import com.doapps.habits.dao.ProgramDao;
import com.doapps.habits.models.Program;

@Database(entities = {Program.class}, version = 1)
public abstract class ProgramsDatabase extends RoomDatabase {

  /**
   * Gets database data access object
   * @return Data Access Object
   */
  public abstract ProgramDao programDao();
}
