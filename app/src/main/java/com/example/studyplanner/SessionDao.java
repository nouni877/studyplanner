package com.example.studyplanner;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SessionDao {

    // Insert new session
    @Insert
    void insert(PlannerSession session);

    // Update existing session (e.g. mark completed)
    @Update
    void update(PlannerSession session);

    // Delete a session
    @Delete
    void deleteSession(PlannerSession session);

    // ✅ All sessions for a specific user (for main list / timetable)
    @Query("SELECT * FROM sessions WHERE username = :username ORDER BY id DESC")
    List<PlannerSession> getAllSessionsForUser(String username);

    // ✅ Get a specific session by ID
    @Query("SELECT * FROM sessions WHERE id = :id LIMIT 1")
    PlannerSession getSessionById(int id);

    // ✅ Total sessions for a specific user (for progress screen)
    @Query("SELECT COUNT(*) FROM sessions WHERE username = :username")
    int getTotalSessionsForUser(String username);

    // ✅ Completed sessions for a specific user (for progress screen)
    @Query("SELECT COUNT(*) FROM sessions WHERE username = :username AND completed = 1")
    int getCompletedSessionsForUser(String username);
}
