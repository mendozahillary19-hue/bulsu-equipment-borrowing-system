package edu.bulsu.equipmentborrowing.ui.returnequip;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import edu.bulsu.equipmentborrowing.R;
import edu.bulsu.equipmentborrowing.model.BorrowTransaction;
import edu.bulsu.equipmentborrowing.model.User;
import edu.bulsu.equipmentborrowing.repository.BorrowRepository;
import edu.bulsu.equipmentborrowing.utils.SessionManager;
import edu.bulsu.equipmentborrowing.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReturnActivity extends AppCompatActivity {

    private BorrowTransaction transaction;
    private String selectedCondition = "Good";
    private String selectedAccessories = "Complete";

    private Calendar returnCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

    private TextView tvName, tvCategory, tvAvailable, tvBorrowerName;
    private TextView tvBorrowedPeriod, tvReturnDate;
    private TextView tvConditionText, tvAccessoriesText;
    private EditText etRemarks;
    private TextView tvSummaryItems, tvSummaryAccessories, tvSummaryTotalQuantity;
    private ImageView ivImage;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        sessionManager = new SessionManager(this);

        transaction = (BorrowTransaction) getIntent().getSerializableExtra("transaction");
        if (transaction == null) {
            // Default demo approved transaction matching Figma Page 23
            transaction = BorrowRepository.getInstance().getTransactionById("TRX-20261009-002");
        }

        initViews();
        bindData();
        updateSummary();
    }

    private void initViews() {
        findViewById(R.id.btnReturnBack).setOnClickListener(v -> finish());

        ivImage = findViewById(R.id.ivReturnEquipmentImage);
        tvName = findViewById(R.id.tvReturnEquipmentName);
        tvCategory = findViewById(R.id.tvReturnEquipmentCategory);
        tvAvailable = findViewById(R.id.tvReturnAvailableStock);
        tvBorrowerName = findViewById(R.id.tvReturnBorrowerName);
        tvBorrowedPeriod = findViewById(R.id.tvReturnBorrowedPeriod);
        tvReturnDate = findViewById(R.id.tvReturnDate);
        tvConditionText = findViewById(R.id.tvReturnConditionText);
        tvAccessoriesText = findViewById(R.id.tvReturnAccessoriesText);
        etRemarks = findViewById(R.id.etReturnRemarks);

        tvSummaryItems = findViewById(R.id.tvReturnSummaryItems);
        tvSummaryAccessories = findViewById(R.id.tvReturnSummaryAccessories);
        tvSummaryTotalQuantity = findViewById(R.id.tvReturnSummaryTotalQuantity);

        findViewById(R.id.btnPickReturnDate).setOnClickListener(v -> showDatePicker());
        findViewById(R.id.btnReturnCondition).setOnClickListener(v -> showConditionDialog());
        findViewById(R.id.btnReturnAccessories).setOnClickListener(v -> showAccessoriesDialog());

        findViewById(R.id.btnSubmitReturnRequest).setOnClickListener(v -> submitReturn());
    }

    private void bindData() {
        tvName.setText(transaction.getEquipmentName());
        tvCategory.setText("Category: " + transaction.getCategoryName());
        tvAvailable.setText("Available: 26");
        ivImage.setImageResource(UiUtils.getEquipmentDrawableId(transaction.getImageName()));

        User user = sessionManager.getUser();
        tvBorrowerName.setText(user.getFullName() != null ? user.getFullName() : "Juan Delacruz");

        String period = transaction.getBorrowDate() + " - " + transaction.getExpectedReturnDate();
        tvBorrowedPeriod.setText(period);

        tvReturnDate.setText(dateFormat.format(returnCalendar.getTime()));
        tvConditionText.setText(selectedCondition);
        tvAccessoriesText.setText(selectedAccessories);
    }

    private void updateSummary() {
        int qty = transaction.getQuantity() != null ? transaction.getQuantity() : 1;
        tvSummaryItems.setText(qty + (qty > 1 ? " items" : " item"));
        tvSummaryAccessories.setText(selectedAccessories.equalsIgnoreCase("Complete") ? "Complete accessory" : selectedAccessories);
        tvSummaryTotalQuantity.setText(qty + (qty > 1 ? " units" : " unit"));
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    returnCalendar.set(year, month, dayOfMonth);
                    tvReturnDate.setText(dateFormat.format(returnCalendar.getTime()));
                },
                returnCalendar.get(Calendar.YEAR),
                returnCalendar.get(Calendar.MONTH),
                returnCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showConditionDialog() {
        String[] options = {"Good", "Fair", "Needs Cleaning / Repair"};
        new AlertDialog.Builder(this)
                .setTitle("Select Condition of Item")
                .setItems(options, (dialog, which) -> {
                    selectedCondition = options[which];
                    tvConditionText.setText(selectedCondition);
                })
                .show();
    }

    private void showAccessoriesDialog() {
        String[] options = {"Complete", "Incomplete", "No Accessory"};
        new AlertDialog.Builder(this)
                .setTitle("Select Accessories Status")
                .setItems(options, (dialog, which) -> {
                    selectedAccessories = options[which];
                    tvAccessoriesText.setText(selectedAccessories);
                    updateSummary();
                })
                .show();
    }

    private void submitReturn() {
        String returnDateStr = tvReturnDate.getText().toString();
        String remarks = etRemarks.getText().toString().trim();

        BorrowRepository.getInstance().submitReturn(
                transaction.getTransactionId(),
                returnDateStr,
                selectedCondition,
                selectedAccessories,
                remarks,
                new BorrowRepository.TransactionCallback() {
                    @Override
                    public void onSuccess(BorrowTransaction updated) {
                        showSuccessDialog(updated);
                    }

                    @Override
                    public void onError(String message) {
                        edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper.showValidationError(
                                ReturnActivity.this, "Return Error", message);
                    }
                }
        );
    }

    private void showSuccessDialog(BorrowTransaction tx) {
        edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper.showSuccessModal(
                this,
                "Equipment Return Submitted!",
                "Transaction ID: " + tx.getTransactionId(),
                "Your return submission for " + tx.getEquipmentName() + " (Qty: " + tx.getQuantity() + ") has been processed. Status is updated to RETURNED and equipment stock has been restored in the laboratory database.",
                this::finish
        );
    }
}
