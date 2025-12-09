package com.example.studyplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView textQuote, emptyMessage;
    private RecyclerView recyclerMainSessions;
    private SessionDatabase sessionDatabase;
    private String currentUser;

    private static final String PREFS_NAME = "StudyPlannerPrefs";
    private static final String KEY_CURRENT_USER = "current_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ---- Find views (strict id match to your XML) ----
        textQuote = findViewById(R.id.textQuote);
        recyclerMainSessions = findViewById(R.id.recyclerMainSessions);
        emptyMessage = findViewById(R.id.emptyMessage);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        FloatingActionButton fabAdd = findViewById(R.id.ic_add);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Safety: if any is null, fail fast with a helpful message.
        if (textQuote == null || recyclerMainSessions == null || emptyMessage == null
                || topAppBar == null || fabAdd == null || bottomNavigationView == null) {
            Toast.makeText(this, "Layout ids do not match activity_main.xml", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // ---- Session / login state ----
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentUser = prefs.getString(KEY_CURRENT_USER, null);



        // Redirect to LoginActivity if not logged in
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // ---- Toolbar menu (requires res/menu/top_app_bar_menu.xml) ----
        topAppBar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_refresh) {
                loadAllSessions();
                Toast.makeText(this, "Sessions refreshed", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_logout) {
                logoutUser();
                return true;
            }
            return false;
        });

        // ---- FAB ----
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddSessionActivity.class);
            startActivity(intent);
        });

        // ---- RecyclerView inside ScrollView tweaks ----
        recyclerMainSessions.setLayoutManager(new LinearLayoutManager(this));
        recyclerMainSessions.setNestedScrollingEnabled(false); // important with ScrollView

        // ---- Load UI data ----
        showRandomQuote();
        loadAllSessions();

        // ---- Bottom Nav (requires res/menu/bottom_nav_menu.xml) ----
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_timetable) {
                startActivity(new Intent(MainActivity.this, TimetableActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_progress) {
                startActivity(new Intent(MainActivity.this, ProgressActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_sessions) {
                startActivity(new Intent(MainActivity.this, AllSessionsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_notes) {
                startActivity(new Intent(MainActivity.this, NotesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllSessions();
    }

    /** Load all study sessions for the current user, safely */
    private void loadAllSessions() {
        new Thread(() -> {
            List<PlannerSession> sessions;
            try {
                if (sessionDatabase == null) {
                    sessionDatabase = SessionDatabase.getInstance(getApplicationContext());
                }
                if (sessionDatabase == null || sessionDatabase.sessionDao() == null) {
                    throw new IllegalStateException("Database not initialized");
                }
                sessions = sessionDatabase.sessionDao().getAllSessionsForUser(currentUser);
                if (sessions == null) sessions = Collections.emptyList();
            } catch (Exception e) {
                final String msg = "Failed to load sessions: " + e.getMessage();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show());
                sessions = Collections.emptyList();
            }

            List<PlannerSession> finalSessions = sessions;
            runOnUiThread(() -> {
                if (!finalSessions.isEmpty()) {
                    SessionAdapter adapter = new SessionAdapter(this, finalSessions, sessionDatabase);
                    recyclerMainSessions.setAdapter(adapter);
                    recyclerMainSessions.setVisibility(View.VISIBLE);
                    emptyMessage.setVisibility(View.GONE);
                } else {
                    recyclerMainSessions.setAdapter(null);
                    recyclerMainSessions.setVisibility(View.GONE);
                    emptyMessage.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }

    /** Logout user — clears only current session */
    private void logoutUser() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().remove(KEY_CURRENT_USER).apply();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /** Display a random motivational quote */
    private void showRandomQuote() {
        String[] quotes = {
                "Push yourself, because no one else is going to do it for you.",
                "Success doesn’t just find you. You have to go out and get it.",
                "Don’t watch the clock; do what it does. Keep going.",
                "The harder you work for something, the greater you’ll feel when you achieve it.",
                "Great things never come from comfort zones.",
                "Study hard, stay humble, and make it happen!"
        };
        Random random = new Random();
        textQuote.setText("\"" + quotes[random.nextInt(quotes.length)] + "\"");
    }
}

