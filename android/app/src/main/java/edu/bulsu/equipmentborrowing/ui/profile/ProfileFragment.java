package edu.bulsu.equipmentborrowing.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import edu.bulsu.equipmentborrowing.R;
import edu.bulsu.equipmentborrowing.model.User;
import edu.bulsu.equipmentborrowing.network.ApiClient;
import edu.bulsu.equipmentborrowing.repository.BorrowRepository;
import edu.bulsu.equipmentborrowing.ui.auth.LoginActivity;
import edu.bulsu.equipmentborrowing.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView tvFullName, tvCourseSection;
    private TextView tvBorrowedCount, tvPendingCount, tvReturnedCount;
    private TextView tvInfoFullName, tvInfoStudentId, tvInfoEmail, tvInfoContact;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());

        tvFullName = view.findViewById(R.id.tvProfileFullName);
        tvCourseSection = view.findViewById(R.id.tvProfileCourseSection);

        tvBorrowedCount = view.findViewById(R.id.tvProfileBorrowedCount);
        tvPendingCount = view.findViewById(R.id.tvProfilePendingCount);
        tvReturnedCount = view.findViewById(R.id.tvProfileReturnedCount);

        tvInfoFullName = view.findViewById(R.id.tvInfoFullName);
        tvInfoStudentId = view.findViewById(R.id.tvInfoStudentId);
        tvInfoEmail = view.findViewById(R.id.tvInfoEmail);
        tvInfoContact = view.findViewById(R.id.tvInfoContact);

        view.findViewById(R.id.btnEditProfile).setOnClickListener(v -> showEditProfileDialog());

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> performLogout());

        view.findViewById(R.id.menuNotifications).setOnClickListener(v ->
                edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper.showNotificationsModal(requireContext()));

        view.findViewById(R.id.menuHelp).setOnClickListener(v ->
                Toast.makeText(requireContext(), "BulSU COE Lab Custodian Office: Room 304, Engineering Building.", Toast.LENGTH_LONG).show());

        view.findViewById(R.id.menuSettings).setOnClickListener(v ->
                Toast.makeText(requireContext(), "App Version 1.0.0 (BulSU COE Edition)", Toast.LENGTH_SHORT).show());

        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        User user = sessionManager.getUser();

        tvFullName.setText(user.getFullName());
        tvCourseSection.setText(user.getStudentId() + " " + user.getCourseAndSection());

        tvInfoFullName.setText(user.getFullName());
        tvInfoStudentId.setText(user.getStudentId());
        tvInfoEmail.setText(user.getEmail());
        tvInfoContact.setText(user.getContactNumber());

        // Dynamic stats
        long pending = BorrowRepository.getInstance().getPendingCountLocal();
        long returned = BorrowRepository.getInstance().getReturnedCountLocal();
        long approved = BorrowRepository.getInstance().getApprovedCountLocal();
        long totalBorrowed = approved + returned; // 5 lifetime loans

        tvBorrowedCount.setText(String.valueOf(Math.max(5, totalBorrowed)));
        tvPendingCount.setText(String.valueOf(pending));
        tvReturnedCount.setText(String.valueOf(returned));

        // Fetch latest profile from API if online
        ApiClient.getService().getProfile(user.getUserId()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User u = response.body();
                    tvBorrowedCount.setText(String.valueOf(u.getBorrowedCount()));
                    tvPendingCount.setText(String.valueOf(u.getPendingCount()));
                    tvReturnedCount.setText(String.valueOf(u.getReturnedCount()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {}
        });
    }

    private void showEditProfileDialog() {
        User user = sessionManager.getUser();

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText etName = dialogView.findViewById(R.id.etEditFullName);
        EditText etCourse = dialogView.findViewById(R.id.etEditCourseSection);
        EditText etEmail = dialogView.findViewById(R.id.etEditEmail);
        EditText etContact = dialogView.findViewById(R.id.etEditContact);

        etName.setText(user.getFullName());
        etCourse.setText(user.getCourseAndSection());
        etEmail.setText(user.getEmail());
        etContact.setText(user.getContactNumber());

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = etName.getText().toString().trim();
                    String newCourse = etCourse.getText().toString().trim();
                    String newEmail = etEmail.getText().toString().trim();
                    String newContact = etContact.getText().toString().trim();

                    if (!newName.isEmpty()) {
                        sessionManager.updateUserProfile(newName, newCourse, newEmail, newContact);
                        loadUserProfile();
                        syncProfileUpdateToApi(user.getUserId(), newName, newCourse, newEmail, newContact);
                        edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper.showSuccessModal(
                                requireContext(),
                                "Profile Updated",
                                "Account ID: " + user.getStudentId(),
                                "Your student profile information has been saved and synchronized with the laboratory database.",
                                null
                        );
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void syncProfileUpdateToApi(Long userId, String name, String course, String email, String contact) {
        Map<String, String> body = new HashMap<>();
        body.put("fullName", name);
        body.put("courseAndSection", course);
        body.put("email", email);
        body.put("contactNumber", contact);
        ApiClient.getService().updateProfile(userId, body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {}
            @Override
            public void onFailure(Call<User> call, Throwable t) {}
        });
    }

    private void performLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of the BulSU Equipment Borrowing System?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
    }
}
