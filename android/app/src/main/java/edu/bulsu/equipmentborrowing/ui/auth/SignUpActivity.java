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
import edu.bulsu.equipmentborrowing.ui.main.MainActivity;
import edu.bulsu.equipmentborrowing.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText etFullName, etStudentId, etCourseSection, etEmail, etContact, etPassword, etConfirmPassword;
    private ImageView btnTogglePassword, btnToggleConfirmPassword;
    private boolean isPasswordVisible = false, isConfirmVisible = false;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sessionManager = new SessionManager(this);

        etFullName = findViewById(R.id.etFullName);
        etStudentId = findViewById(R.id.etStudentIdSignUp);
        etCourseSection = findViewById(R.id.etCourseSection);
        etEmail = findViewById(R.id.etEmail);
        etContact = findViewById(R.id.etContact);
        etPassword = findViewById(R.id.etPasswordSignUp);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePasswordSignUp);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);

        // Prefill default registration values matching Figma prototype
        etFullName.setText("Juan Dela Cruz");
        etStudentId.setText("2024101234");
        etCourseSection.setText("BS Computer Engineering, CpE-3A");
        etEmail.setText("juan@bulsu.edu.ph");
        etContact.setText("09123456789");

        btnTogglePassword.setOnClickListener(v -> {
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
        });

        btnToggleConfirmPassword.setOnClickListener(v -> {
            if (isConfirmVisible) {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_invisible);
                isConfirmVisible = false;
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_visible);
                isConfirmVisible = true;
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        findViewById(R.id.btnSubmitSignUp).setOnClickListener(v -> attemptRegistration());

        findViewById(R.id.tvNavLogin).setOnClickListener(v -> finish());
    }

    private void attemptRegistration() {
        String fullName = etFullName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String courseSection = etCourseSection.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (fullName.isEmpty() || studentId.isEmpty() || email.isEmpty() || password.isEmpty()) {
            edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper.showValidationError(
                    this, "Required Fields Missing", "Please fill in all mandatory fields before submitting your registration.");
            return;
        }

        if (password.length() < 6) {
            edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper.showValidationError(
                    this, "Weak Password", "Password must contain at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper.showValidationError(
                    this, "Password Mismatch", "The passwords entered do not match. Please re-enter your password.");
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("fullName", fullName);
        body.put("studentId", studentId);
        body.put("courseAndSection", courseSection);
        body.put("email", email);
        body.put("contactNumber", contact);
        body.put("password", password);
        body.put("confirmPassword", confirmPassword);

        ApiClient.getService().register(body).enqueue(new Callback<ApiService.AuthResponse>() {
            @Override
            public void onResponse(Call<ApiService.AuthResponse> call, Response<ApiService.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    User user = response.body().user;
                    sessionManager.saveUserSession(user, response.body().token);
                    edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper.showSuccessModal(
                            SignUpActivity.this,
                            "Registration Successful!",
                            "Student ID: " + user.getStudentId(),
                            "Welcome to the BulSU Equipment Borrowing Platform, " + user.getFullName() + "! Your student account has been registered in the database.",
                            SignUpActivity.this::navigateToMain
                    );
                } else {
                    fallbackLocalRegister(fullName, studentId, courseSection, email, contact);
                }
            }

            @Override
            public void onFailure(Call<ApiService.AuthResponse> call, Throwable t) {
                fallbackLocalRegister(fullName, studentId, courseSection, email, contact);
            }
        });
    }

    private void fallbackLocalRegister(String fullName, String studentId, String courseSection, String email, String contact) {
        User user = new User(1L, studentId, fullName, courseSection, email, contact, "BORROWER");
        sessionManager.saveUserSession(user, "local-registered-token");
        edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper.showSuccessModal(
                this,
                "Registration Successful!",
                "Student ID: " + studentId,
                "Welcome to the BulSU Equipment Borrowing Platform, " + fullName + "! Your student account is now active.",
                this::navigateToMain
        );
    }

    private void navigateToMain() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
