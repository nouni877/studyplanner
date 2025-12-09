package com.example.studyplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AccountActivity extends AppCompatActivity {

    private TextView textUsername, textMotivation;
    private Button btnLogout;

    private static final String PREFS_NAME = "StudyPlannerPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_CURRENT_USER = "current_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        //  start UI elements
        textUsername = findViewById(R.id.textUsername);
        textMotivation = findViewById(R.id.textMotivation);
        btnLogout = findViewById(R.id.btnLogout);

        //  Retrieve the *current* logged-in user
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String currentUser = prefs.getString(KEY_CURRENT_USER, "User");

      // welcome message
        textUsername.setText("Welcome, " + currentUser + "!");


        //  Display a motivational message
        textMotivation.setText("Stay consistent — every small step counts!");

        // Logout logic
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(KEY_CURRENT_USER);
            editor.apply();

            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

