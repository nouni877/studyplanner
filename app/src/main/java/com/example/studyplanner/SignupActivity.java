package com.example.studyplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText editNewUsername, editPassword;
    private Button btnCreateAccount;
    private ImageButton btnBackToLogin;
    private TextView textLoginInstead;

    private static final String PREFS_NAME = "StudyPlannerPrefs";
    private static final String KEY_CURRENT_USER = "current_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editNewUsername = findViewById(R.id.editNewUsername);
        editPassword = findViewById(R.id.editPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        textLoginInstead = findViewById(R.id.textLoginInstead);

        btnCreateAccount.setOnClickListener(v -> createAccount());
        textLoginInstead.setOnClickListener(v ->
                startActivity(new Intent(SignupActivity.this, LoginActivity.class)));
        btnBackToLogin.setOnClickListener(v ->
                startActivity(new Intent(SignupActivity.this, LoginActivity.class)));
    }

    private void createAccount() {
        String username = editNewUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (prefs.contains(username)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(username, password);
        editor.putString(KEY_CURRENT_USER, username);
        editor.apply();

        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SignupActivity.this, MainActivity.class));
        finish();
    }
}


