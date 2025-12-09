package com.example.studyplanner;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "Note")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String username;   // owner
    private int subjectId;     // FK to Subject.id
    private String content;

    // The ONLY constructor Room should use
    public Note(String username, int subjectId, String content) {
        this.username = username;
        this.subjectId = subjectId;
        this.content = content;
    }

    // (Optional) convenience constructor — NOT for Room
    @Ignore
    public Note(String username, String content) {
        this(username, 0, content); // subjectId 0 if you ever need it
    }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
