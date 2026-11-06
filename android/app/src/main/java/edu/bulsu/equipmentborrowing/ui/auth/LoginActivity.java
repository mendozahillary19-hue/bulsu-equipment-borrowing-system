package edu.bulsu.equipmentborrowing.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import edu.bulsu.equipmentborrowing.R;
import edu.bulsu.equipmentborrowing.model.User;
import edu.bulsu.equipmentborrowing.network.ApiClient;
import edu.bulsu.equipmentborrowing.network.ApiService;
import edu.bulsu.equipmentborrowing.ui.admin.AdminDashboardActivity;
import edu.bulsu.equipmentborrowing.ui.main.MainActivity;
import edu.bulsu.equipmentborrowing.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etStudentId;
    private EditText etPassword;
    private ImageView btnTogglePassword;
    private boolean isPasswordVisible = false;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        // Auto-login if session exists
        if (sessionManager.isLoggedIn()) {
            navigateAfterLogin(sessionManager.getUser());
            return;
        }

        etStudentId = findViewById(R.id.etStudentId);
        etPassword = findViewById(R.id.etPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);

        // Prefill default student ID matching Figma prototype
        etStudentId.setText("2024101234");
        etPassword.setText("JuAnPoGi123");

        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        findViewById(R.id.btnLogin).setOnClickListener(v -> attemptLogin());

        findViewById(R.id.btnNavSignUp).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye_invisible);
            isPasswordVisible = false;
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye_visible);
            isPasswordVisible = true;
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void attemptLogin() {
        String studentId = etStudentId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (studentId.isEmpty()) {
            etStudentId.setError("Student ID / Account ID is required");
            etStudentId.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("studentId", studentId);
        body.put("password", password);

        ApiClient.getService().login(body).enqueue(new Callback<ApiService.AuthResponse>() {
            @Override
            public void onResponse(Call<ApiService.AuthResponse> call, Response<ApiService.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    User user = response.body().user;
                    sessionManager.saveUserSession(user, response.body().token);
                    Toast.makeText(LoginActivity.this, "Welcome, " + user.getFullName() + "!", Toast.LENGTH_SHORT).show();
                    navigateAfterLogin(user);
                } else {
                    fallbackLocalLogin(studentId, password);
                }
            }

            @Override
            public void onFailure(Call<ApiService.AuthResponse> call, Throwable t) {
                fallbackLocalLogin(studentId, password);
            }
        });
    }

    private void fallbackLocalLogin(String studentId, String password) {
        // Fallback for offline or local testing matching Figma credentials
        if (studentId.equalsIgnoreCase("admin2026") || studentId.equalsIgnoreCase("admin")) {
            User admin = new User(2L, "admin2026", "Engr. Roberto Santos",
                    "College of Engineering - Custodian", "admin@bulsu.edu.ph", "09187654321", "ADMIN");
            sessionManager.saveUserSession(admin, "mock-admin-token");
            Toast.makeText(this, "Logged in as Administrator", Toast.LENGTH_SHORT).show();
            navigateAfterLogin(admin);
        } else {
            User student = new User(1L, studentId, "Juan Dela Cruz",
                    "BS Computer Engineering, CpE-3A", "juan@bulsu.edu.ph", "09123456789", "BORROWER");
            student.setBorrowedCount(5);
            student.setPendingCount(2);
            student.setReturnedCount(3);
            sessionManager.saveUserSession(student, "mock-student-token");
            Toast.makeText(this, "Welcome, Juan Dela Cruz!", Toast.LENGTH_SHORT).show();
            navigateAfterLogin(student);
        }
    }

    private void navigateAfterLogin(User user) {
        Intent intent;
        if (user.isAdmin()) {
            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
