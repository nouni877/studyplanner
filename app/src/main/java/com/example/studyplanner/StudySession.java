package com.example.studyplanner;

public class StudySession {
    private String title;
    private String time;
    private String notes;
    private String day;

    public StudySession() {}

    public StudySession(String title, String time, String notes, String day) {
        this.title = title;
        this.time = time;
        this.notes = notes;
        this.day = day;
    }

    public String getTitle() { return title; }
    public String getTime() { return time; }
    public String getNotes() { return notes; }
    public String getDay() { return day; }
}

