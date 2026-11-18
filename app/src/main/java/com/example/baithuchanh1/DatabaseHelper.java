package com.example.baithuchanh1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "appointments.db";
    public static final int DATABASE_VERSION = 1;

    // Table users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";

    // Table appointments
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String COLUMN_APP_ID = "id";
    public static final String COLUMN_APP_TITLE = "title";
    public static final String COLUMN_APP_LOCATION = "location";
    public static final String COLUMN_APP_DATE = "date";
    public static final String COLUMN_APP_TIME = "time";
    public static final String COLUMN_APP_REMINDER = "reminder";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users
        String createTableUsers = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COLUMN_USER_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(createTableUsers);

        // Tạo bảng appointments
        String createTableAppointments = "CREATE TABLE " + TABLE_APPOINTMENTS + " ("
                + COLUMN_APP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_APP_TITLE + " TEXT NOT NULL, "
                + COLUMN_APP_LOCATION + " TEXT NOT NULL, "
                + COLUMN_APP_DATE + " TEXT NOT NULL, "
                + COLUMN_APP_TIME + " TEXT NOT NULL, "
                + COLUMN_APP_REMINDER + " INTEGER NOT NULL)";
        db.execSQL(createTableAppointments);

        // Chèn tài khoản mẫu (admin / 123456)
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USER_USERNAME, "admin");
        userValues.put(COLUMN_USER_PASSWORD, "123456");
        db.insert(TABLE_USERS, null, userValues);

        // Chèn 3 lịch hẹn mẫu
        insertSampleAppointment(db, "Họp nhóm đồ án", "Phòng B203", "2026-10-25", "09:00", 1);
        insertSampleAppointment(db, "Khám răng", "Nha khoa Minh Tâm", "2026-10-26", "14:30", 1);
        insertSampleAppointment(db, "Đi cà phê với bạn", "Highland Coffee", "2026-10-27", "19:00", 0);
    }

    private void insertSampleAppointment(SQLiteDatabase db, String title, String location, String date, String time, int reminder) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_TITLE, title);
        values.put(COLUMN_APP_LOCATION, location);
        values.put(COLUMN_APP_DATE, date);
        values.put(COLUMN_APP_TIME, time);
        values.put(COLUMN_APP_REMINDER, reminder);
        db.insert(TABLE_APPOINTMENTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        onCreate(db);
    }

    /**
     * Kiểm tra thông tin đăng nhập từ bảng users
     */
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isValid = false;
        try {
            cursor = db.query(TABLE_USERS,
                    new String[]{COLUMN_USER_ID},
                    COLUMN_USER_USERNAME + " = ? AND " + COLUMN_USER_PASSWORD + " = ?",
                    new String[]{username, password},
                    null, null, null);
            isValid = (cursor != null && cursor.getCount() > 0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isValid;
    }

    /**
     * Thêm tài khoản mới vào bảng users
     */
    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    /**
     * Thêm lịch hẹn mới vào bảng appointments
     */
    public long insertAppointment(Appointment a) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_TITLE, a.getTitle());
        values.put(COLUMN_APP_LOCATION, a.getLocation());
        values.put(COLUMN_APP_DATE, a.getDate());
        values.put(COLUMN_APP_TIME, a.getTime());
        values.put(COLUMN_APP_REMINDER, a.getReminder());
        return db.insert(TABLE_APPOINTMENTS, null, values);
    }

    /**
     * Lấy toàn bộ danh sách lịch hẹn, sắp xếp tăng dần theo date ASC, time ASC
     */
    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_APPOINTMENTS,
                    null,
                    null,
                    null,
                    null,
                    null,
                    COLUMN_APP_DATE + " ASC, " + COLUMN_APP_TIME + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APP_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APP_TITLE));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APP_LOCATION));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APP_DATE));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APP_TIME));
                    int reminder = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APP_REMINDER));

                    list.add(new Appointment(id, title, location, date, time, reminder));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * Lấy thông tin một lịch hẹn theo id
     */
    public Appointment getAppointmentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Appointment appointment = null;
        try {
            cursor = db.query(TABLE_APPOINTMENTS,
                    null,
                    COLUMN_APP_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APP_TITLE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APP_LOCATION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APP_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APP_TIME));
                int reminder = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APP_REMINDER));

                appointment = new Appointment(id, title, location, date, time, reminder);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return appointment;
    }

    /**
     * Cập nhật trạng thái nhắc nhở cho một lịch hẹn
     */
    public int updateReminder(int id, boolean on) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_REMINDER, on ? 1 : 0);
        return db.update(TABLE_APPOINTMENTS, values, COLUMN_APP_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Xóa một lịch hẹn theo id
     */
    public int deleteAppointment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_APPOINTMENTS, COLUMN_APP_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Đếm tổng số lịch hẹn
     */
    public int countAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    /**
     * Đếm số lịch hẹn đang bật nhắc nhở (reminder = 1)
     */
    public int countReminders() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS + " WHERE " + COLUMN_APP_REMINDER + " = 1", null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
}
