package com.example.baithuchanh1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    private final Context context;
    private ArrayList<Appointment> list;
    private final OnAppointmentClickListener listener;

    public AppointmentAdapter(Context context, ArrayList<Appointment> list, OnAppointmentClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void setData(ArrayList<Appointment> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = list.get(position);
        holder.tvTitle.setText(appointment.getTitle());
        holder.tvLocation.setText(appointment.getLocation());
        holder.tvDateTime.setText(appointment.getFormattedDateTime());

        if (appointment.isReminder()) {
            holder.tvReminder.setText("Có nhắc");
            holder.tvReminder.setBackgroundResource(R.drawable.bg_badge_reminder_on);
            holder.tvReminder.setTextColor(ContextCompat.getColor(context, R.color.chip_reminder_on_text));
        } else {
            holder.tvReminder.setText("Không nhắc");
            holder.tvReminder.setBackgroundResource(R.drawable.bg_badge_reminder_off);
            holder.tvReminder.setTextColor(ContextCompat.getColor(context, R.color.chip_reminder_off_text));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAppointmentClick(appointment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvDateTime, tvReminder;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvLocation = itemView.findViewById(R.id.tvItemLocation);
            tvDateTime = itemView.findViewById(R.id.tvItemDateTime);
            tvReminder = itemView.findViewById(R.id.tvItemReminder);
        }
    }
}
