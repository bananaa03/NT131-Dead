package com.example.dead;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Ánh xạ các thành phần giao diện
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonRegister = findViewById(R.id.buttonRegister);

        // Xử lý sự kiện khi nhấn nút Đăng ký
        buttonRegister.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Kiểm tra hợp lệ
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đăng ký người dùng mới trên Firebase
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            // Chuyển về LoginActivity
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Đăng ký thất bại
                            Toast.makeText(getApplicationContext(), "Đăng ký thất bại: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
