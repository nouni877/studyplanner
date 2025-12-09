package com.example.studyplanner;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM Note WHERE username = :username ORDER BY id DESC")
    List<Note> getNotesForUser(String username);

    @Query("SELECT * FROM Note WHERE username = :username AND subjectId = :subjectId ORDER BY id DESC")
    List<Note> getNotesForUserAndSubject(String username, int subjectId);

}
