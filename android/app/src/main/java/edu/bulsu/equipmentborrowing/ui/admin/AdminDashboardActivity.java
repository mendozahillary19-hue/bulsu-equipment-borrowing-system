package edu.bulsu.equipmentborrowing.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.bulsu.equipmentborrowing.R;
import edu.bulsu.equipmentborrowing.model.BorrowTransaction;
import edu.bulsu.equipmentborrowing.network.ApiClient;
import edu.bulsu.equipmentborrowing.repository.BorrowRepository;
import edu.bulsu.equipmentborrowing.repository.EquipmentRepository;
import edu.bulsu.equipmentborrowing.ui.auth.LoginActivity;
import edu.bulsu.equipmentborrowing.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tabAll, tabPending, tabApproved, tabReturned;
    private RecyclerView rvTransactions;
    private AdminTransactionAdapter adapter;
    private SessionManager sessionManager;
    private String currentFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        sessionManager = new SessionManager(this);

        tabAll = findViewById(R.id.tabAdminAll);
        tabPending = findViewById(R.id.tabAdminPending);
        tabApproved = findViewById(R.id.tabAdminApproved);
        tabReturned = findViewById(R.id.tabAdminReturned);
        rvTransactions = findViewById(R.id.rvAdminTransactions);

        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminTransactionAdapter(this, new AdminTransactionAdapter.AdminActionListener() {
            @Override
            public void onApprove(BorrowTransaction tx) {
                showApproveDialog(tx);
            }

            @Override
            public void onReject(BorrowTransaction tx) {
                showRejectDialog(tx);
            }
        });
        rvTransactions.setAdapter(adapter);

        findViewById(R.id.btnAdminBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAdminLogout).setOnClickListener(v -> logout());

        tabAll.setOnClickListener(v -> setFilter("ALL"));
        tabPending.setOnClickListener(v -> setFilter("PENDING"));
        tabApproved.setOnClickListener(v -> setFilter("APPROVED"));
        tabReturned.setOnClickListener(v -> setFilter("RETURNED"));

        updateTabUi();
        loadTransactions();
    }

    private void setFilter(String filter) {
        this.currentFilter = filter;
        updateTabUi();
        loadTransactions();
    }

    private void updateTabUi() {
        int selectedBg = R.drawable.bg_category_chip_selected;
        int unselectedBg = R.drawable.bg_category_chip;

        int selectedColor = ContextCompat.getColor(this, R.color.tab_selected_text);
        int unselectedColor = ContextCompat.getColor(this, R.color.tab_unselected_text);

        tabAll.setBackgroundResource(unselectedBg); tabAll.setTextColor(unselectedColor);
        tabPending.setBackgroundResource(unselectedBg); tabPending.setTextColor(unselectedColor);
        tabApproved.setBackgroundResource(unselectedBg); tabApproved.setTextColor(unselectedColor);
        tabReturned.setBackgroundResource(unselectedBg); tabReturned.setTextColor(unselectedColor);

        switch (currentFilter) {
            case "PENDING":
                tabPending.setBackgroundResource(selectedBg); tabPending.setTextColor(selectedColor);
                break;
            case "APPROVED":
                tabApproved.setBackgroundResource(selectedBg); tabApproved.setTextColor(selectedColor);
                break;
            case "RETURNED":
                tabReturned.setBackgroundResource(selectedBg); tabReturned.setTextColor(selectedColor);
                break;
            case "ALL":
            default:
                tabAll.setBackgroundResource(selectedBg); tabAll.setTextColor(selectedColor);
                break;
        }
    }

    private void loadTransactions() {
        ApiClient.getService().getAdminTransactions(currentFilter).enqueue(new Callback<List<BorrowTransaction>>() {
            @Override
            public void onResponse(Call<List<BorrowTransaction>> call, Response<List<BorrowTransaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setTransactions(response.body());
                } else {
                    adapter.setTransactions(BorrowRepository.getInstance().filterMockTransactions(currentFilter));
                }
            }

            @Override
            public void onFailure(Call<List<BorrowTransaction>> call, Throwable t) {
                adapter.setTransactions(BorrowRepository.getInstance().filterMockTransactions(currentFilter));
            }
        });
    }

    private void showApproveDialog(BorrowTransaction tx) {
        new AlertDialog.Builder(this)
                .setTitle("Approve Equipment Loan?")
                .setMessage("Transaction: " + tx.getTransactionId() + "\nItem: " + tx.getEquipmentName() + " (" + tx.getQuantity() + " unit)\nBorrower: " + tx.getBorrowerName() + "\n\nApproving will record checkout and deduct available inventory.")
                .setPositiveButton("Approve", (dialog, which) -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("adminId", sessionManager.getUser().getUserId());
                    body.put("remarks", "Approved by Engr. Roberto Santos. Ready for pickup.");

                    ApiClient.getService().approveTransaction(tx.getTransactionId(), body).enqueue(new Callback<BorrowTransaction>() {
                        @Override
                        public void onResponse(Call<BorrowTransaction> call, Response<BorrowTransaction> response) {
                            onApproveSuccess(tx);
                        }

                        @Override
                        public void onFailure(Call<BorrowTransaction> call, Throwable t) {
                            onApproveSuccess(tx);
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void onApproveSuccess(BorrowTransaction tx) {
        tx.setStatus("APPROVED");
        EquipmentRepository.getInstance().deductStockLocal(tx.getEquipmentId(), tx.getQuantity());
        edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper.showSuccessModal(
                this,
                "Transaction Approved",
                "Transaction ID: " + tx.getTransactionId(),
                "The borrowing request for " + tx.getEquipmentName() + " has been approved. Available inventory has been updated.",
                this::loadTransactions
        );
    }

    private void showRejectDialog(BorrowTransaction tx) {
        edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper.showAdminRemarksDialog(
                this,
                "Reject Borrow Request",
                tx.getTransactionId(),
                true,
                remarks -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("adminId", sessionManager.getUser().getUserId());
                    body.put("reason", remarks);

                    ApiClient.getService().rejectTransaction(tx.getTransactionId(), body).enqueue(new Callback<BorrowTransaction>() {
                        @Override
                        public void onResponse(Call<BorrowTransaction> call, Response<BorrowTransaction> response) {
                            onRejectSuccess(tx, remarks);
                        }

                        @Override
                        public void onFailure(Call<BorrowTransaction> call, Throwable t) {
                            onRejectSuccess(tx, remarks);
                        }
                    });
                }
        );
    }

    private void onRejectSuccess(BorrowTransaction tx, String remarks) {
        tx.setStatus("REJECTED");
        tx.setAdminRemarks(remarks);
        edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper.showSuccessModal(
                this,
                "Request Rejected",
                "Transaction ID: " + tx.getTransactionId(),
                "Borrow request has been marked as REJECTED with remarks: \"" + remarks + "\".",
                this::loadTransactions
        );
    }

    private void logout() {
        sessionManager.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
