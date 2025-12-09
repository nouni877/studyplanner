package com.example.studyplanner;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SubjectDao {
    @Insert
    long insert(Subject subject);

    @Query("SELECT * FROM Subject WHERE username = :username ORDER BY name ASC")
    List<Subject> getSubjectsForUser(String username);

    @Query("SELECT * FROM Subject WHERE id = :id LIMIT 1")
    Subject getById(int id);
}
