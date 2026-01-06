package com.example.studyplanner;
// This class represents a task within the study planner application. It is used as a
// data model to store task-related information such as the task title, description,
// date, and time, allowing tasks to be displayed and managed within the app.
public class Task {
    private String title;
    private String description;
    private String date;
    private String time;

    public Task(String title, String description, String date, String time) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}
