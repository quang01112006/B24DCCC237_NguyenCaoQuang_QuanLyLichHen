package com.example.baithuchanh1;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Appointment implements Serializable {
    private int id;
    private String title;
    private String location;
    private String date; // Lưu dạng yyyy-MM-dd để sắp xếp đúng
    private String time; // Lưu dạng HH:mm
    private int reminder; // 1: bật nhắc nhở, 0: tắt

    public Appointment() {
    }

    public Appointment(String title, String location, String date, String time, int reminder) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    public Appointment(int id, String title, String location, String date, String time, int reminder) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getReminder() {
        return reminder;
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }

    public boolean isReminder() {
        return reminder == 1;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder ? 1 : 0;
    }

    /**
     * Chuyển đổi định dạng ngày giờ yyyy-MM-dd và HH:mm thành dd/MM/yyyy · HH:mm
     */
    public String getFormattedDateTime() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy · HH:mm", Locale.getDefault());
            Date parsedDate = inputFormat.parse(this.date + " " + this.time);
            if (parsedDate != null) {
                return outputFormat.format(parsedDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return this.date + " · " + this.time;
    }
}
