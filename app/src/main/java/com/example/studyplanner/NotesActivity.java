package com.example.studyplanner;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.view.View;

public class NotesActivity extends AppCompatActivity {

    private EditText editNoteInput;
    private Button btnAddNote, btnAddSubject;
    private RecyclerView recyclerNotes;
    private Spinner spinnerSubjects;

    private SessionDatabase db;
    private NoteDao noteDao;
    private SubjectDao subjectDao;

    private String currentUser;
    private List<Subject> subjects = new ArrayList<>();
    private ArrayAdapter<String> subjectAdapter;

    private static final String PREFS_NAME = "StudyPlannerPrefs";
    private static final String KEY_CURRENT_USER = "current_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        Toolbar toolbar = findViewById(R.id.toolbarNotes);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getNavigationIcon().setTint(getResources().getColor(android.R.color.white)); // ✅ force white tint
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentUser = prefs.getString(KEY_CURRENT_USER, null);

        db = SessionDatabase.getInstance(this);
        noteDao = db.noteDao();
        subjectDao = db.subjectDao();

        editNoteInput   = findViewById(R.id.editNoteInput);
        btnAddNote      = findViewById(R.id.btnAddNote);
        btnAddSubject   = findViewById(R.id.btnAddSubject);
        recyclerNotes   = findViewById(R.id.recyclerNotes);
        spinnerSubjects = findViewById(R.id.spinnerSubjects);

        recyclerNotes.setLayoutManager(new LinearLayoutManager(this));

        // spinner adapter with subject names
        subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        spinnerSubjects.setAdapter(subjectAdapter);

        loadSubjectsAndNotes();

        btnAddSubject.setOnClickListener(v -> openAddSubjectDialog());

        btnAddNote.setOnClickListener(v -> {
            String text = editNoteInput.getText().toString().trim();
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(this, "Enter a note", Toast.LENGTH_SHORT).show();
                return;
            }
            if (subjects.isEmpty()) {
                Toast.makeText(this, "Create a subject first", Toast.LENGTH_SHORT).show();
                return;
            }
            int idx = spinnerSubjects.getSelectedItemPosition();
            int subjectId = subjects.get(idx).getId();

            new Thread(() -> {
                noteDao.insert(new Note(currentUser, subjectId, text));
                runOnUiThread(() -> {
                    editNoteInput.setText("");
                    loadNotesForSubject(subjectId);
                    Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });
    }

    private void loadSubjectsAndNotes() {
        new Thread(() -> {
            subjects = subjectDao.getSubjectsForUser(currentUser);
            runOnUiThread(() -> {
                subjectAdapter.clear();
                for (Subject s : subjects) subjectAdapter.add(s.getName());
                subjectAdapter.notifyDataSetChanged();

                if (!subjects.isEmpty()) loadNotesForSubject(subjects.get(0).getId());
                else recyclerNotes.setAdapter(new NotesAdapter(new ArrayList<>(), subjectDao));
            });
        }).start();
    }

    private void loadNotesForSubject(int subjectId) {
        new Thread(() -> {
            List<Note> notes = noteDao.getNotesForUserAndSubject(currentUser, subjectId);
            runOnUiThread(() -> recyclerNotes.setAdapter(new NotesAdapter(notes, subjectDao)));
        }).start();
    }

    private void openAddSubjectDialog() {
        var view = LayoutInflater.from(this).inflate(R.layout.dialog_add_subject, null, false);
        EditText editSubjectName = view.findViewById(R.id.editSubjectName);
        View c1 = view.findViewById(R.id.color1);
        View c2 = view.findViewById(R.id.color2);
        View c3 = view.findViewById(R.id.color3);

        final int[] chosenColor = { getColor(R.color.navy) };
        c1.setOnClickListener(v -> chosenColor[0] = getColor(R.color.navy));
        c2.setOnClickListener(v -> chosenColor[0] = getColor(R.color.green_accent));
        c3.setOnClickListener(v -> chosenColor[0] = Color.parseColor("#FFC107"));

        new AlertDialog.Builder(this)
                .setTitle("New Subject")
                .setView(view)
                .setPositiveButton("Save", (d, which) -> {
                    String name = editSubjectName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Enter a subject name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new Thread(() -> {
                        subjectDao.insert(new Subject(currentUser, name, chosenColor[0]));
                        runOnUiThread(this::loadSubjectsAndNotes);
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
