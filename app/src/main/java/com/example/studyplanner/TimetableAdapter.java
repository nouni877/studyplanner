package com.example.studyplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {

    private Context context;
    private List<PlannerSession> sessions;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(PlannerSession session);
        void onDeleteClick(PlannerSession session);
    }

    public TimetableAdapter(Context context, List<PlannerSession> sessions) {
        this.context = context;
        this.sessions = sessions;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlannerSession session = sessions.get(position);

        //  Bind data to layout views
        holder.tvTitle.setText(session.getTitle());
        holder.tvDescription.setText(session.getNotes());
        holder.tvDateTime.setText(session.getDay() + " • " + session.getTime());

        // Set up Edit/Delete button actions if listener is defined
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(session);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(session);
        });
    }

    @Override
    public int getItemCount() {
        return sessions != null ? sessions.size() : 0;
    }

    // Only one ViewHolder class
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDateTime;
        Button btnEdit, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
