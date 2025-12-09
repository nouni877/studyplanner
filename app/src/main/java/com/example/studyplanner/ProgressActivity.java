package com.example.studyplanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;

public class ProgressActivity extends AppCompatActivity {

    private CircularProgressIndicator circularProgress;
    private ProgressBar weeklyProgress;
    private TextView tvPercentage, tvSessions, tvMotivation;
    private SessionDatabase sessionDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        //  Initialize views
        circularProgress = findViewById(R.id.circularProgress);
        weeklyProgress = findViewById(R.id.weeklyProgress);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvSessions = findViewById(R.id.tvSessions);
        tvMotivation = findViewById(R.id.tvMotivation);

        //  Reset Progress Button
        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> resetProgress());

        //  Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        //  Get database instance
        sessionDatabase = SessionDatabase.getInstance(this);

        // Load progress data
        loadProgress();
    }

    /**
     * Loads progress data from the Room database asynchronously.
     */
    private void loadProgress() {
        new Thread(() -> {
            //  Get current logged-in user
            SharedPreferences prefs = getSharedPreferences("StudyPlannerPrefs", MODE_PRIVATE);
            String currentUser = prefs.getString("current_user", "");

            //  Load only that user's sessions
            List<PlannerSession> sessions = sessionDatabase.sessionDao().getAllSessionsForUser(currentUser);

            int totalSessions = sessions.size();
            int completedSessions = 0;

            for (PlannerSession s : sessions) {
                if (s.isCompleted()) completedSessions++;
            }

            int percentage = totalSessions > 0
                    ? (int) ((completedSessions / (float) totalSessions) * 100)
                    : 0;

            final int finalTotal = totalSessions;
            final int finalCompleted = completedSessions;
            final int finalPercentage = percentage;

            runOnUiThread(() -> updateUI(finalPercentage, finalCompleted, finalTotal));
        }).start();
    }

    /**
     * Resets progress by marking all sessions as incomplete.
     */
    private void resetProgress() {
        new Thread(() -> {
            //  Get current logged-in user
            SharedPreferences prefs = getSharedPreferences("StudyPlannerPrefs", MODE_PRIVATE);
            String currentUser = prefs.getString("username", "");
            List<PlannerSession> sessions = sessionDatabase.sessionDao().getAllSessionsForUser(currentUser);
            for (PlannerSession s : sessions) {
                s.setCompleted(false);
                sessionDatabase.sessionDao().update(s);
            }

            //  Refresh UI
            runOnUiThread(this::loadProgress);
        }).start();
    }

    /**
     * Updates the progress UI with calculated percentage and motivation.
     */
    private void updateUI(int percentage, int completed, int total) {
        circularProgress.setProgress(percentage, true);
        weeklyProgress.setProgress(percentage);
        tvPercentage.setText(percentage + "%");
        tvSessions.setText(completed + " of " + total + " sessions completed");

        if (total == 0) {
            tvMotivation.setText("No sessions yet! Add your first one to get started 💪");
        } else if (percentage < 30) {
            tvMotivation.setText("Keep going — every bit counts! 📚");
        } else if (percentage < 70) {
            tvMotivation.setText("You’re making great progress — stay consistent ✨");
        } else if (percentage < 100) {
            tvMotivation.setText("Almost there! Just a few more to complete 💥");
        } else {
            tvMotivation.setText("Perfect! You’ve completed all your sessions 🎯");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProgress(); // 🔥 Refresh every time you return to the screen
    }

}


