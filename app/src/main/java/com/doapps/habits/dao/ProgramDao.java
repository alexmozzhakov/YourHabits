package com.doapps.habits.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.doapps.habits.models.Program;

import java.util.List;

@Dao
public interface ProgramDao {
    @Query("SELECT * FROM program")
    List<Program> getAll();

    @Insert
    void insertAll(Program... programs);

    @Query("SELECT EXISTS(SELECT 1 FROM program WHERE program_id = :programId LIMIT 1)")
    int idExists(int programId);
}