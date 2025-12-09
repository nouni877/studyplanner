package com.example.studyplanner;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sessions")
public class PlannerSession {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String username; // 👈 owner of the session
    private String title;
    private String day;
    private String time;
    private String notes;
    private boolean completed;

    // ✅ Empty constructor (required by Room)
    public PlannerSession() {
    }

    // ✅ Custom constructor for easier creation
    public PlannerSession(String username, String title, String day, String time, String notes, boolean completed) {
        this.username = username;
        this.title = title;
        this.day = day;
        this.time = time;
        this.notes = notes;
        this.completed = completed;
    }

    // ----- Getters and Setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
