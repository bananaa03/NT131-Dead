package com.example.dead;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        // Xử lý sự kiện khi nhấn nút "Đăng nhập"
        buttonLogin.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            // Xác thực người dùng
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                            /*
                            // Hiển thị tài khoản đang đăng nhập
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            if (currentUser != null) {
                                String email1 = currentUser.getEmail();
                                setContentView(R.layout.activity_main);
                                TextView userTextView = findViewById(R.id.userTextView);
                                userTextView.setText(email1);
                            }*/

                            // Quay lại màn hình chính
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Xử lý sự kiện khi nhấn nút "Đăng ký"
        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(v -> {
            // Xử lý sự kiện khi người dùng nhấn vào nút "Đăng ký"
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}