package com.example.studyplanner;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteVH> {

    private final List<Note> notes;
    private final SubjectDao subjectDao; // to resolve subject name/color

    public NotesAdapter(List<Note> notes, SubjectDao subjectDao) {
        this.notes = notes;
        this.subjectDao = subjectDao;
    }

    @NonNull
    @Override
    public NoteVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteVH h, int pos) {
        Note n = notes.get(pos);
        h.textNote.setText(n.getContent());

        // resolve subject
        new Thread(() -> {
            Subject s = subjectDao.getById(n.getSubjectId());
            h.itemView.post(() -> {
                if (s != null) {
                    h.textSubjectName.setText(s.getName());
                    GradientDrawable d = (GradientDrawable) h.viewDot.getBackground().mutate();
                    d.setColor(s.getColor());
                } else {
                    h.textSubjectName.setText("Subject");
                }
            });
        }).start();
    }

    @Override
    public int getItemCount() { return notes.size(); }

    static class NoteVH extends RecyclerView.ViewHolder {
        TextView textNote, textSubjectName;
        View viewDot;
        NoteVH(@NonNull View itemView) {
            super(itemView);
            textNote = itemView.findViewById(R.id.textNote);
            textSubjectName = itemView.findViewById(R.id.textSubjectName);
            viewDot = itemView.findViewById(R.id.viewSubjectDot);
        }
    }
}
