package com.example.studyplanner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import android.content.SharedPreferences;


public class AddEditActivity extends AppCompatActivity {

    private EditText editTitle, editDescription;
    private TextView textDate, textTime;
    private Button btnSave;
    private SessionDatabase db;

    private int sessionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        textDate = findViewById(R.id.textDate);
        textTime = findViewById(R.id.textTime);
        btnSave = findViewById(R.id.btnSave);

        db = SessionDatabase.getInstance(this);

        // Check if editing an existing session
        if (getIntent().hasExtra("id")) {
            sessionId = getIntent().getIntExtra("id", -1);
            editTitle.setText(getIntent().getStringExtra("title"));
            editDescription.setText(getIntent().getStringExtra("description"));
            textDate.setText(getIntent().getStringExtra("date"));
            textTime.setText(getIntent().getStringExtra("time"));
        }

        textDate.setOnClickListener(v -> showDatePicker());
        textTime.setOnClickListener(v -> showTimePicker());

        btnSave.setOnClickListener(v -> saveSession());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                textDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) ->
                textTime.setText(String.format("%02d:%02d", hourOfDay, minute)),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
        ).show();
    }

    private void saveSession() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String date = textDate.getText().toString();
        String time = textTime.getText().toString();

        if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("StudyPlannerPrefs", MODE_PRIVATE);
        String currentUser = prefs.getString("username", "");

        if (sessionId == -1) {
            db.sessionDao().insert(new PlannerSession(currentUser, title, date, time, description, false));
            Toast.makeText(this, "Session Added", Toast.LENGTH_SHORT).show();
        } else {
            PlannerSession plannerSession = new PlannerSession(currentUser, title, date, time, description, false);
            plannerSession.setId(sessionId);
            db.sessionDao().update(plannerSession);
            Toast.makeText(this, "Session Updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
