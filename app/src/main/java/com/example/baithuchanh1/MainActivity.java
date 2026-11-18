package com.example.baithuchanh1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalAppointments;
    private TextView tvTotalReminders;
    private RecyclerView rvAppointments;
    private LinearLayout layoutEmpty;
    private Button btnAddAppointment;

    private DatabaseHelper dbHelper;
    private AppointmentAdapter adapter;
    private ArrayList<Appointment> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Xử lý tai thỏ / notch và thanh trạng thái
        View rootMain = findViewById(R.id.rootMain);
        if (rootMain != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootMain, (v, insets) -> {
                Insets systemBars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
                );
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        dbHelper = new DatabaseHelper(this);

        tvTotalAppointments = findViewById(R.id.tvTotalAppointments);
        tvTotalReminders = findViewById(R.id.tvTotalReminders);
        rvAppointments = findViewById(R.id.rvAppointments);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnAddAppointment = findViewById(R.id.btnAddAppointment);

        // Cài đặt RecyclerView
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(this, appointmentList, appointment -> {
            // Mở màn hình chi tiết, truyền id của lịch hẹn qua Intent
            Intent intent = new Intent(MainActivity.this, AppointmentDetailActivity.class);
            intent.putExtra("appointment_id", appointment.getId());
            startActivity(intent);
        });
        rvAppointments.setAdapter(adapter);

        // Mở màn hình thêm lịch hẹn
        btnAddAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddAppointmentActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại danh sách và cập nhật thống kê khi quay lại màn hình
        loadDataAndStatistics();
    }

    private void loadDataAndStatistics() {
        // Lấy danh sách lịch hẹn từ database (sắp xếp tăng dần theo date ASC, time ASC)
        appointmentList = dbHelper.getAllAppointments();
        adapter.setData(appointmentList);

        // Xử lý hiển thị trạng thái rỗng
        if (appointmentList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvAppointments.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvAppointments.setVisibility(View.VISIBLE);
        }

        // Cập nhật thống kê từ database
        int total = dbHelper.countAll();
        int reminders = dbHelper.countReminders();

        tvTotalAppointments.setText(String.valueOf(total));
        tvTotalReminders.setText(String.valueOf(reminders));
    }
}
