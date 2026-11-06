package edu.bulsu.equipmentborrowing.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import edu.bulsu.equipmentborrowing.R;
import edu.bulsu.equipmentborrowing.model.BorrowTransaction;
import edu.bulsu.equipmentborrowing.ui.returnequip.ReturnActivity;
import edu.bulsu.equipmentborrowing.utils.UiUtils;

public class RequestDetailActivity extends AppCompatActivity {

    private BorrowTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        transaction = (BorrowTransaction) getIntent().getSerializableExtra("transaction");
        if (transaction == null) {
            finish();
            return;
        }

        findViewById(R.id.btnDetailBack).setOnClickListener(v -> finish());

        TextView tvTxId = findViewById(R.id.tvDetailTransactionId);
        TextView tvStatusBadge = findViewById(R.id.tvDetailStatusBadge);
        ImageView ivImage = findViewById(R.id.ivDetailEquipmentImage);
        TextView tvName = findViewById(R.id.tvDetailEquipmentName);
        TextView tvCategory = findViewById(R.id.tvDetailCategory);
        TextView tvQuantity = findViewById(R.id.tvDetailQuantity);
        TextView tvBorrowDate = findViewById(R.id.tvDetailBorrowDate);
        TextView tvReturnDate = findViewById(R.id.tvDetailExpectedReturnDate);
        TextView tvCondition = findViewById(R.id.tvDetailCondition);
        TextView tvAccessories = findViewById(R.id.tvDetailAccessories);
        TextView tvAdminRemarks = findViewById(R.id.tvDetailAdminRemarks);
        TextView tvNextAction = findViewById(R.id.tvDetailNextAction);
        Button btnReturn = findViewById(R.id.btnDetailReturnEquipment);

        tvTxId.setText(transaction.getTransactionId());
        tvStatusBadge.setText(transaction.getStatus());
        tvStatusBadge.setBackgroundResource(UiUtils.getStatusBgDrawable(transaction.getStatus()));
        tvStatusBadge.setTextColor(UiUtils.getStatusTextColor(this, transaction.getStatus()));

        ivImage.setImageResource(UiUtils.getEquipmentDrawableId(transaction.getImageName()));
        tvName.setText(transaction.getEquipmentName());
        tvCategory.setText("Category: " + transaction.getCategoryName());
        tvQuantity.setText("Quantity: " + transaction.getQuantity() + " unit" + (transaction.getQuantity() > 1 ? "s" : ""));

        tvBorrowDate.setText(transaction.getBorrowDate());
        tvReturnDate.setText(transaction.getExpectedReturnDate());

        tvCondition.setText(transaction.getBorrowCondition() != null ? transaction.getBorrowCondition() : "Good");
        tvAccessories.setText(transaction.getBorrowAccessories() != null ? transaction.getBorrowAccessories() : "None");

        if (transaction.getAdminRemarks() != null && !transaction.getAdminRemarks().isEmpty()) {
            tvAdminRemarks.setText(transaction.getAdminRemarks());
        } else {
            tvAdminRemarks.setText("No administrator remarks recorded yet.");
        }

        // Determine Next Action Required & Return Button visibility
        String status = transaction.getStatus().toUpperCase();
        switch (status) {
            case "PENDING":
                tvNextAction.setText("Your request has been submitted. Please wait for Lab Custodian approval before claiming equipment.");
                btnReturn.setVisibility(View.GONE);
                break;
            case "APPROVED":
                tvNextAction.setText("Equipment is checked out. Please ensure it is returned on or before " + transaction.getExpectedReturnDate() + ".");
                btnReturn.setVisibility(View.VISIBLE);
                btnReturn.setOnClickListener(v -> {
                    Intent intent = new Intent(RequestDetailActivity.this, ReturnActivity.class);
                    intent.putExtra("transaction", transaction);
                    startActivity(intent);
                    finish();
                });
                break;
            case "RETURNED":
                tvNextAction.setText("Equipment has been returned and verified by the custodian. No further action needed.");
                btnReturn.setVisibility(View.GONE);
                break;
            case "REJECTED":
                tvNextAction.setText("Request was declined. Check administrator remarks above or visit the College of Engineering Lab Custodian office.");
                btnReturn.setVisibility(View.GONE);
                break;
            case "OVERDUE":
                tvNextAction.setText("URGENT: Equipment is past due date! Return immediately to avoid laboratory suspension.");
                btnReturn.setVisibility(View.VISIBLE);
                break;
            default:
                tvNextAction.setText("Status: " + status);
                btnReturn.setVisibility(View.GONE);
                break;
        }
    }
}
