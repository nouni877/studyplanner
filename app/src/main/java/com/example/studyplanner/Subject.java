package com.example.studyplanner;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Subject")
public class Subject {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String username;
    private String name;
    private int color;

    public Subject(String username, String name, int color) {
        this.username = username;
        this.name = name;
        this.color = color;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
}
