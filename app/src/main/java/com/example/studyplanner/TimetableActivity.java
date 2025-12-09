package com.example.studyplanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableActivity extends AppCompatActivity {

    private SessionDatabase db;
    private LinearLayout timetableContainer;

    private final String[] days = {
            "Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday", "Sunday"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        timetableContainer = findViewById(R.id.timetableContainer);
        db = SessionDatabase.getInstance(this);

        loadTimetable(); // initial load
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTimetable();   // 🔥 refresh every time user returns
    }

    private void loadTimetable() {
        new Thread(() -> {
            SharedPreferences prefs = getSharedPreferences("StudyPlannerPrefs", MODE_PRIVATE);
            String currentUser = prefs.getString("current_user", "");

            List<PlannerSession> sessions = db.sessionDao().getAllSessionsForUser(currentUser);

            // Group sessions by day
            Map<String, StringBuilder> daySessions = new HashMap<>();
            for (String day : days) {
                daySessions.put(day, new StringBuilder());
            }

            if (sessions != null) {
                for (PlannerSession s : sessions) {
                    if (daySessions.containsKey(s.getDay())) {

                        // Show ✔ if completed, • if not
                        String icon = s.isCompleted() ? "✔ " : "• ";

                        daySessions.get(s.getDay())
                                .append(icon)
                                .append(s.getTitle())
                                .append(" at ")
                                .append(s.getTime());

                        if (s.isCompleted()) {
                            daySessions.get(s.getDay()).append(" (done)");
                        }

                        daySessions.get(s.getDay()).append("\n");
                    }
                }
            }

            runOnUiThread(() -> {
                timetableContainer.removeAllViews();

                for (String day : days) {
                    LinearLayout row = new LinearLayout(this);
                    row.setOrientation(LinearLayout.VERTICAL);
                    row.setPadding(24, 20, 24, 20);
                    row.setBackgroundResource(R.drawable.card_background);

                    // Day Title
                    TextView dayTitle = new TextView(this);
                    dayTitle.setText(day);
                    dayTitle.setTextSize(18);
                    dayTitle.setTextColor(getColor(R.color.navy_blue));
                    dayTitle.setPadding(0, 0, 0, 8);

                    // Sessions for the day
                    TextView sessionText = new TextView(this);
                    String content = daySessions.get(day).toString().trim();

                    if (content.isEmpty()) {
                        sessionText.setText("No sessions");
                        sessionText.setTextColor(getColor(android.R.color.darker_gray));
                    } else {
                        sessionText.setText(content);
                        sessionText.setTextColor(getColor(android.R.color.black));
                    }

                    sessionText.setTextSize(15);

                    row.addView(dayTitle);
                    row.addView(sessionText);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 8, 0, 8);

                    timetableContainer.addView(row, params);
                }
            });
        }).start();
    }
}

