package com.example.baithuchanh1;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private ImageButton btnBackAdd;
    private EditText etTitle;
    private EditText etLocation;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private SwitchMaterial switchReminder;
    private Button btnSaveAppointment;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Đảm bảo app luôn hiển thị chuẩn theme sáng, không bị Dark Mode làm mờ chữ
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        // Xử lý tai thỏ / notch và thanh trạng thái
        View rootAdd = findViewById(R.id.rootAdd);
        if (rootAdd != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootAdd, (v, insets) -> {
                Insets systemBars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
                );
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        dbHelper = new DatabaseHelper(this);

        btnBackAdd = findViewById(R.id.btnBackAdd);
        etTitle = findViewById(R.id.etTitle);
        etLocation = findViewById(R.id.etLocation);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        switchReminder = findViewById(R.id.switchReminder);
        btnSaveAppointment = findViewById(R.id.btnSaveAppointment);

        // Hiển thị định dạng 24 giờ cho TimePicker
        timePicker.setIs24HourView(true);

        // Ép màu chữ đen tuyền, tương phản cao tuyệt đối cho DatePicker & TimePicker
        applyHighContrastToPicker(datePicker);
        applyHighContrastToPicker(timePicker);

        btnBackAdd.setOnClickListener(v -> finish());
        btnSaveAppointment.setOnClickListener(v -> handleSaveAppointment());
    }

    /**
     * Tối ưu độ tương phản cho DatePicker & TimePicker trên các dòng máy (Oppo, Xiaomi, Samsung...)
     */
    private void applyHighContrastToPicker(ViewGroup viewGroup) {
        if (viewGroup == null) return;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof NumberPicker) {
                NumberPicker np = (NumberPicker) child;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    np.setTextColor(Color.BLACK);
                }
                for (int j = 0; j < np.getChildCount(); j++) {
                    View npChild = np.getChildAt(j);
                    if (npChild instanceof EditText) {
                        EditText et = (EditText) npChild;
                        et.setTextColor(Color.BLACK);
                        et.setFocusable(false);
                    }
                }
            } else if (child instanceof ViewGroup) {
                applyHighContrastToPicker((ViewGroup) child);
            }
        }
    }

    private void handleSaveAppointment() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        // 1. Kiểm tra tên lịch hẹn không được để trống
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Tên lịch hẹn không được để trống");
            etTitle.requestFocus();
            return;
        }

        // 2. Kiểm tra địa điểm không được để trống
        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Địa điểm không được để trống");
            etLocation.requestFocus();
            return;
        }

        // 3. Lấy thông tin ngày và giờ được chọn
        int year = datePicker.getYear();
        int month = datePicker.getMonth(); // 0 - 11
        int day = datePicker.getDayOfMonth();

        int hour;
        int minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        // 4. Kiểm tra thời điểm hẹn phải sau thời điểm hiện tại
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.set(year, month, day, hour, minute, 0);
        selectedCal.set(Calendar.MILLISECOND, 0);

        Calendar currentCal = Calendar.getInstance();

        if (selectedCal.getTimeInMillis() <= currentCal.getTimeInMillis()) {
            Toast.makeText(this, "Thời điểm hẹn (ngày và giờ) phải sau thời điểm hiện tại!", Toast.LENGTH_LONG).show();
            return;
        }

        // 5. Chuẩn hóa dữ liệu ngày (yyyy-MM-dd) và giờ (HH:mm)
        String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
        String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        int reminder = switchReminder.isChecked() ? 1 : 0;

        // 6. Lưu vào cơ sở dữ liệu SQLite
        Appointment newAppointment = new Appointment(title, location, dateStr, timeStr, reminder);
        long newId = dbHelper.insertAppointment(newAppointment);

        if (newId != -1) {
            Toast.makeText(this, "Lưu lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Quay về màn hình danh sách
        } else {
            Toast.makeText(this, "Có lỗi xảy ra khi lưu lịch hẹn!", Toast.LENGTH_SHORT).show();
        }
    }
}
