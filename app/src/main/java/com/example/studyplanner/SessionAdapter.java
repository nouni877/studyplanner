package com.example.studyplanner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private final Context context;
    private final List<PlannerSession> sessions;
    private final SessionDatabase db;

    public SessionAdapter(Context context, List<PlannerSession> sessions, SessionDatabase db) {
        this.context = context;
        this.sessions = sessions;
        this.db = db;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        PlannerSession session = sessions.get(position);

        holder.textTitle.setText(session.getTitle());
        holder.textDayTime.setText(session.getDay() + " - " + session.getTime());
        holder.textNotes.setText(session.getNotes().isEmpty() ? "No notes" : session.getNotes());

        // Completed state visuals
        if (session.isCompleted()) {
            holder.btnMarkCompleted.setText("Completed");
            holder.btnMarkCompleted.setBackgroundTintList(
                    ContextCompat.getColorStateList(context, android.R.color.holo_green_dark));
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.holo_green_light));
        } else {
            holder.btnMarkCompleted.setText("Mark Completed");
            holder.btnMarkCompleted.setBackgroundTintList(
                    ContextCompat.getColorStateList(context, R.color.navy_blue));
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.white));
        }

        // Mark Completed toggle
        holder.btnMarkCompleted.setOnClickListener(v -> {
            boolean newStatus = !session.isCompleted();
            session.setCompleted(newStatus);

            new Thread(() -> {
                db.sessionDao().update(session);
                ((AppCompatActivity) context).runOnUiThread(() -> notifyItemChanged(holder.getAdapterPosition()));
            }).start();
        });

        // Edit Session
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddSessionActivity.class);
            intent.putExtra("isEditing", true);
            intent.putExtra("sessionId", session.getId());
            context.startActivity(intent);
        });

        // Delete Session
        holder.btnDelete.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Delete Session")
                .setMessage("Are you sure you want to delete this session?")
                .setPositiveButton("Yes", (dialog, which) -> new Thread(() -> {
                    db.sessionDao().deleteSession(session);
                    int pos = holder.getAdapterPosition();
                    sessions.remove(pos);
                    ((AppCompatActivity) context).runOnUiThread(() -> notifyItemRemoved(pos));
                }).start())
                .setNegativeButton("Cancel", null)
                .show());
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDayTime, textNotes;
        Button btnMarkCompleted, btnEdit, btnDelete;
        CardView cardView;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textSessionTitle);
            textDayTime = itemView.findViewById(R.id.textSessionTime);
            textNotes = itemView.findViewById(R.id.textSessionDescription);
            btnMarkCompleted = itemView.findViewById(R.id.btnMarkCompleted);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            cardView = itemView.findViewById(R.id.cardSession);
        }
    }
}


