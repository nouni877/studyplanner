package com.example.studyplanner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class AddSessionActivity extends AppCompatActivity {

    private EditText editTitle, editNotes;
    private Button btnSelectDate, btnSelectTime, btnSave;
    private Spinner spinnerDay;
    private SessionDatabase db;
    private String selectedDate = "";
    private String currentUser = "";

    private static final String PREFS_NAME = "StudyPlannerPrefs";
    private static final String KEY_CURRENT_USER = "current_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        // ----- Toolbar -----
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // ----- Initialize UI components -----
        editTitle = findViewById(R.id.editTitle);
        editNotes = findViewById(R.id.editNotes);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnSave = findViewById(R.id.btnSave);
        spinnerDay = findViewById(R.id.spinnerDay);

        // ----- Get current logged-in username -----
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentUser = prefs.getString(KEY_CURRENT_USER, null);

        if (currentUser == null || currentUser.isEmpty()) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ----- Initialize database -----
        db = SessionDatabase.getInstance(this);

        // ----- Set listeners -----
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnSave.setOnClickListener(v -> saveSessionDirectly());
    }

    /** Date Picker **/
    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    btnSelectDate.setText(selectedDate);
                },
                year, month, day
        );
        datePicker.show();
    }

    /** Time Picker **/
    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) ->
                        btnSelectTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute)),
                hour, minute, true);
        timePicker.show();
    }

    /** Save the session instantly and close the page **/
    private void saveSessionDirectly() {
        String title = editTitle.getText().toString().trim();
        String time = btnSelectTime.getText().toString();
        String notes = editNotes.getText().toString().trim();
        String day = spinnerDay.getSelectedItem().toString();

        // ----- Validation -----
        if (title.isEmpty() || time.equals("Select Time") || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields before saving.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser.isEmpty()) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // ----- Save session in background thread -----
        new Thread(() -> {
            // ✅ Add username so this session belongs only to the logged-in user
            PlannerSession session = new PlannerSession();
            session.setUsername(currentUser);
            session.setTitle(title);
            session.setDay(day);
            session.setTime(time);
            session.setNotes(notes);
            session.setCompleted(false);

            db.sessionDao().insert(session);

            runOnUiThread(() -> {
                Toast.makeText(this, "Session added successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
