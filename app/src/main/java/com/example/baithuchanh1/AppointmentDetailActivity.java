package com.example.baithuchanh1;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class AppointmentDetailActivity extends AppCompatActivity {

    private ImageButton btnBackTop;
    private TextView tvDetailTitle;
    private TextView tvDetailLocation;
    private TextView tvDetailTime;
    private TextView tvReminderStatus;
    private SwitchMaterial switchReminderDetail;
    private Button btnBack;
    private Button btnDelete;

    private DatabaseHelper dbHelper;
    private int appointmentId = -1;
    private Appointment currentAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        // Xử lý tai thỏ / notch và thanh trạng thái
        View rootDetail = findViewById(R.id.rootDetail);
        if (rootDetail != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootDetail, (v, insets) -> {
                Insets systemBars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
                );
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        dbHelper = new DatabaseHelper(this);

        btnBackTop = findViewById(R.id.btnBackTop);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        tvDetailTime = findViewById(R.id.tvDetailTime);
        tvReminderStatus = findViewById(R.id.tvReminderStatus);
        switchReminderDetail = findViewById(R.id.switchReminderDetail);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        // Nhận appointment_id từ Intent
        appointmentId = getIntent().getIntExtra("appointment_id", -1);

        if (appointmentId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin lịch hẹn!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Truy vấn thông tin lịch hẹn từ cơ sở dữ liệu theo id
        loadAppointmentDetail();

        // Nút quay lại trên thanh tiêu đề và nút Quay lại ở dưới
        btnBackTop.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());

        // Lắng nghe sự kiện bật/tắt Switch nhắc nhở
        switchReminderDetail.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dbHelper.updateReminder(appointmentId, isChecked);
            updateReminderStatusText(isChecked);
            Toast.makeText(this, isChecked ? "Đã bật nhắc nhở" : "Đã tắt nhắc nhở", Toast.LENGTH_SHORT).show();
        });

        // Xử lý nút xóa lịch hẹn
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void loadAppointmentDetail() {
        currentAppointment = dbHelper.getAppointmentById(appointmentId);
        if (currentAppointment == null) {
            Toast.makeText(this, "Lịch hẹn không tồn tại hoặc đã bị xóa!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailTitle.setText(currentAppointment.getTitle());
        tvDetailLocation.setText(currentAppointment.getLocation());
        tvDetailTime.setText(currentAppointment.getFormattedDateTime());

        boolean isReminder = currentAppointment.isReminder();
        switchReminderDetail.setChecked(isReminder);
        updateReminderStatusText(isReminder);
    }

    private void updateReminderStatusText(boolean isReminder) {
        if (isReminder) {
            tvReminderStatus.setText("Đang bật");
            tvReminderStatus.setTextColor(getColor(R.color.text_primary));
        } else {
            tvReminderStatus.setText("Đang tắt");
            tvReminderStatus.setTextColor(getColor(R.color.text_muted));
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa lịch hẹn")
                .setMessage("Bạn có chắc chắn muốn xóa lịch hẹn này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int rowsDeleted = dbHelper.deleteAppointment(appointmentId);
                    if (rowsDeleted > 0) {
                        Toast.makeText(this, "Đã xóa lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // Đóng màn hình chi tiết và quay lại danh sách
                    } else {
                        Toast.makeText(this, "Không thể xóa lịch hẹn!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
