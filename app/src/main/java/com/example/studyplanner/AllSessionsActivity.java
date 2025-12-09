package com.example.studyplanner;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.content.SharedPreferences;


public class AllSessionsActivity extends AppCompatActivity {

    private SessionDatabase sessionDatabase;
    private RecyclerView recyclerSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sessions);

        // Setup toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerSessions = findViewById(R.id.recyclerSessions);
        recyclerSessions.setLayoutManager(new LinearLayoutManager(this));

        sessionDatabase = SessionDatabase.getInstance(this);
        loadSessions();
    }

    private void loadSessions() {
        new Thread(() -> {
            // Get current logged-in user
            SharedPreferences prefs = getSharedPreferences("StudyPlannerPrefs", MODE_PRIVATE);
            String currentUser = prefs.getString("current_user", "");

            // Load only that user’s sessions
            List<PlannerSession> sessions = sessionDatabase.sessionDao().getAllSessionsForUser(currentUser);

            runOnUiThread(() -> {
                if (sessions != null && !sessions.isEmpty()) {
                    SessionAdapter adapter = new SessionAdapter(this, sessions, sessionDatabase);
                    recyclerSessions.setAdapter(adapter);
                } else {
                    recyclerSessions.setAdapter(null);
                }
            });
        }).start();

    }

}
