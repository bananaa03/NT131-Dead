package com.example.dead;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private TextView textViewContent;
    private Button buttonDismiss;
    private NotificationHelper notificationHelper;

    public double temperature;
    public double humidity;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        textViewContent = findViewById(R.id.textViewContent);
        buttonDismiss = findViewById(R.id.buttonDismiss);

        notificationHelper = new NotificationHelper(this);
        notificationHelper.createNotificationChannel();

        // Kết nối với Firebase Realtime Database và lắng nghe sự thay đổi
        FirebaseDatabase.getInstance().getReference("sensor_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lấy nhiệt độ và độ ẩm từ Firebase
                double temperature = dataSnapshot.child("temperature").getValue(Double.class);
                double humidity = dataSnapshot.child("humidity").getValue(Double.class);

                // Kiểm tra nhiệt độ và độ ẩm
                if (temperature < 0 || temperature > 10 || humidity < 65 || humidity > 75) {
                    // Hiển thị thông báo lên TextView
                    textViewContent.setText("Cảnh báo: Nhiệt độ hoặc độ ẩm không nằm trong khoảng quy định.");
                    showNotification("Cảnh báo", "Nhiệt độ hoặc độ ẩm không nằm trong khoảng quy định.");
                } else {
                    // Nếu nhiệt độ và độ ẩm nằm trong khoảng yêu cầu, xóa nội dung TextView
                    textViewContent.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
            }
        });

        // Xử lý sự kiện khi nhấn nút "Đóng thông báo"
        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng Activity và quay trở lại màn hình trước đó
                textViewContent.setText("");
                finish();
            }
        });
    }
    private void showNotification(String title, String message) {
        notificationHelper.showNotification(title, message);
    }
}