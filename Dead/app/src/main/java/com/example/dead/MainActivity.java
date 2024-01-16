package com.example.dead;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch lightSwitch1, lightSwitch2, lightSwitch3;
    private FirebaseAuth firebaseAuth;
    public TextView temperatureTextView;
    public TextView humidityTextView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightSwitch1 = findViewById(R.id.lightSwitch1);
        lightSwitch2 = findViewById(R.id.lightSwitch2);
        lightSwitch3 = findViewById(R.id.lightSwitch3);
        Button updateButton = findViewById(R.id.updateButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button loginButton = findViewById(R.id.loginButton);
        Button notificationButton = findViewById(R.id.notificationButton);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        humidityTextView= findViewById(R.id.humidityTextView);

        // Khai báo DatabaseReference cho các đèn
        DatabaseReference light1Ref = FirebaseDatabase.getInstance().getReference("lights/light1");
        DatabaseReference light2Ref = FirebaseDatabase.getInstance().getReference("lights/light2");
        DatabaseReference light3Ref = FirebaseDatabase.getInstance().getReference("lights/light3");

        // Lắng nghe sự thay đổi của 3 đèn
        light1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isLight1On = dataSnapshot.getValue(Boolean.class);
                // Xử lý trạng thái đèn 1
                lightSwitch1.setChecked(isLight1On != null && isLight1On); // Đèn 1 bật
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần thiết
            }
        });

        light2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isLight2On = dataSnapshot.getValue(Boolean.class);
                // Xử lý sự thay đổi trạng thái đèn 2 tại đây
                // Đèn 2 đã bật
                // Đèn 2 đã tắt
                lightSwitch2.setChecked(isLight2On != null && isLight2On);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần thiết
            }
        });

        light3Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isLight3On = dataSnapshot.getValue(Boolean.class);
                // Xử lý sự thay đổi trạng thái đèn 3 tại đây
                // Đèn 3 đã bật
                // Đèn 3 đã tắt
                lightSwitch3.setChecked(isLight3On != null && isLight3On);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần thiết
            }
        });

        updateButton.setOnClickListener(v -> {
            // Xử lý logic cập nhật nhiệt độ và độ ẩm từ Firebase
            updateTemperatureAndHumidity();
        });

        // Xử lý đăng xuất
        firebaseAuth = FirebaseAuth.getInstance();
        logoutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Toast.makeText(MainActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        });

        lightSwitch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Xử lý logic bật/tắt đèn 1
            if (isChecked) {
                turnOnLight1();
            } else {
                turnOffLight1();
            }
        });

        lightSwitch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Xử lý logic bật/tắt đèn 2
            if (isChecked) {
                turnOnLight2();
            } else {
                turnOffLight2();
            }
        });

        lightSwitch3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Xử lý logic bật/tắt đèn 3
            if (isChecked) {
                turnOnLight3();
            } else {
                turnOffLight3();
            }
        });

        loginButton.setOnClickListener(v -> {
            // Chuyển sang giao diện Activity Login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        notificationButton.setOnClickListener(v -> {
            // Chuyển sang giao diện Activity Notification
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
    }

    private void updateTemperatureAndHumidity() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("sensor_data");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lấy dữ liệu nhiệt độ và độ ẩm từ dataSnapshot
                    double temperature = dataSnapshot.child("temperature").getValue(Double.class);
                    double humidity = dataSnapshot.child("humidity").getValue(Double.class);

                    // Kiểm tra nhiệt độ và độ ẩm
                    if (temperature >= 0 && temperature <= 10 && humidity >= 65 && humidity <= 75) {
                        // Hiển thị nhiệt độ và độ ẩm trong giao diện người dùng
                        temperatureTextView.setText(String.valueOf(temperature));
                        humidityTextView.setText(String.valueOf(humidity));
                    } else {
                        // Gửi cảnh báo qua email
                        temperatureTextView.setText(String.valueOf(temperature));
                        humidityTextView.setText(String.valueOf(humidity));
                        Toast.makeText(getApplicationContext(), "Cảnh báo: Nhiệt độ, độ ẩm không nằm trong khoảng quy định!", Toast.LENGTH_SHORT).show();
                        //sendNotificationEmail();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi khi không thể đọc dữ liệu từ Firebase
                temperatureTextView.setText("Null");
                humidityTextView.setText("Null");
            }
        });
    }
    private void sendNotificationEmail() {
        // Lấy thông tin email người dùng từ Firebase Authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userEmail = user.getEmail();

        // Thiết lập thông tin SendGrid
        String sendGridApiKey = "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";
        Email from = new Email("hnbc.chau@gmail.com"); // Địa chỉ email của bạn
        String subject = "Thông báo: Nhiệt độ và độ ẩm không nằm trong khoảng quy định!";
        Email to = new Email(userEmail); // Địa chỉ email của người dùng

        // Tạo nội dung email
        Content content = new Content("text/plain", "Nhiệt độ và độ ẩm không nằm trong khoảng quy định!");

        // Tạo email
        Mail mail = new Mail(from, subject, to, content);

        // Khởi tạo SendGrid
        SendGrid sendGrid = new SendGrid(sendGridApiKey);

        // Gửi email bất đồng bộ
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);

            if (response.getStatusCode() == 202) {
                // Gửi email thành công
                Log.d("SendGrid", "Email sent successfully");
            } else {
                // Gửi email không thành công
                Log.d("SendGrid", "Email sending failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý lỗi khi gửi email
        }
    }

    private void turnOnLight1() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("lights/light1");
        databaseRef.setValue(true);
    }

    private void turnOffLight1() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("lights/light1");
        databaseRef.setValue(false);
    }

    private void turnOnLight2() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("lights/light2");
        databaseRef.setValue(true);
    }

    private void turnOffLight2() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("lights/light2");
        databaseRef.setValue(false);
    }

    private void turnOnLight3() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("lights/light3");
        databaseRef.setValue(true);
    }

    private void turnOffLight3() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("lights/light3");
        databaseRef.setValue(false);
    }
}