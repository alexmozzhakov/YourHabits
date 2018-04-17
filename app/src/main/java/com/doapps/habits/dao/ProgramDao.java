package com.doapps.habits.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.doapps.habits.models.Program;
import java.util.List;

/**
 * The Program database DAO.
 */
@Dao
public interface ProgramDao {

  /**
   * Gets program list from a database
   *
   * @return all programs in a database
   */
  @Query("SELECT * FROM program")
  List<Program> getAll();

  /**
   * Inserts list to a database
   *
   * @param programs A list of programs to be added
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(Program... programs);

  /**
   * Checks whether a given id exists in database
   *
   * @param id An id to check
   * @return 1 or 0 for true and false accordingly
   */
  @Query("SELECT EXISTS(SELECT 1 FROM program WHERE id = :id LIMIT 1)")
  int idExists(int id);

  /**
   * Deletes an entity from a database
   *
   * @param habitId An id of a habit which is related with program to be deleted
   */
  @Query("DELETE FROM program WHERE habit_id = :habitId")
  void delete(int habitId);
}