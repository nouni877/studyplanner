package com.example.studyplanner;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {PlannerSession.class, Note.class, Subject.class},
        version = 4,           // ✅ bump this when you change any entity
        exportSchema = false
)
public abstract class SessionDatabase extends RoomDatabase {

    private static SessionDatabase instance;

    // ---- DAOs ----
    public abstract SessionDao sessionDao();
    public abstract NoteDao noteDao();
    public abstract SubjectDao subjectDao();

    // ---- Singleton getter ----
    public static synchronized SessionDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            SessionDatabase.class,
                            "session_database"
                    )
                    // ✅ This wipes and recreates the DB if schema/version changes,
                    // avoiding "Room cannot verify the data integrity" crashes.
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}

