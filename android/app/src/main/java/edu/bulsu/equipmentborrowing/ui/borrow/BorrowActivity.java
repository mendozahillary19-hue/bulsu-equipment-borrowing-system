package edu.bulsu.equipmentborrowing.ui.borrow;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import edu.bulsu.equipmentborrowing.R;
import edu.bulsu.equipmentborrowing.model.BorrowTransaction;
import edu.bulsu.equipmentborrowing.model.Equipment;
import edu.bulsu.equipmentborrowing.model.User;
import edu.bulsu.equipmentborrowing.repository.BorrowRepository;
import edu.bulsu.equipmentborrowing.repository.EquipmentRepository;
import edu.bulsu.equipmentborrowing.ui.main.MainActivity;
import edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper;
import edu.bulsu.equipmentborrowing.utils.SessionManager;
import edu.bulsu.equipmentborrowing.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BorrowActivity extends AppCompatActivity {

    private Equipment equipment;
    private int currentQuantity = 1;
    private String selectedAccessories = "None";
    private String selectedCondition = "Good";

    private Calendar borrowCalendar = Calendar.getInstance();
    private Calendar returnCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

    private TextView tvEquipmentName, tvEquipmentCategory, tvAvailableStock;
    private ImageView ivEquipmentImage;
    private TextView tvQuantityCount, tvBorrowerName, tvBorrowDate, tvExpectedReturnDate;
    private TextView tvBorrowCondition, tvBorrowAccessories;
    private TextView tvSummaryItems, tvSummaryAccessories, tvSummaryTotalQuantity;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);

        sessionManager = new SessionManager(this);

        equipment = (Equipment) getIntent().getSerializableExtra("equipment");
        if (equipment == null) {
            equipment = EquipmentRepository.getInstance().getEquipmentByIdLocal(1);
        }

        // Return date defaults to 4 days from borrow date
        returnCalendar.add(Calendar.DAY_OF_MONTH, 4);

        initViews();
        bindEquipmentData();
        bindUserData();
        updateSummary();
    }

    private void initViews() {
        findViewById(R.id.btnBorrowBack).setOnClickListener(v -> finish());

        ivEquipmentImage = findViewById(R.id.ivBorrowEquipmentImage);
        tvEquipmentName = findViewById(R.id.tvBorrowEquipmentName);
        tvEquipmentCategory = findViewById(R.id.tvBorrowEquipmentCategory);
        tvAvailableStock = findViewById(R.id.tvBorrowAvailableStock);

        tvQuantityCount = findViewById(R.id.tvQuantityCount);
        tvBorrowerName = findViewById(R.id.tvBorrowerName);
        tvBorrowDate = findViewById(R.id.tvBorrowDate);
        tvExpectedReturnDate = findViewById(R.id.tvExpectedReturnDate);
        tvBorrowCondition = findViewById(R.id.tvBorrowCondition);
        tvBorrowAccessories = findViewById(R.id.tvBorrowAccessories);

        tvSummaryItems = findViewById(R.id.tvSummaryItems);
        tvSummaryAccessories = findViewById(R.id.tvSummaryAccessories);
        tvSummaryTotalQuantity = findViewById(R.id.tvSummaryTotalQuantity);

        // Stepper buttons
        findViewById(R.id.btnQuantityMinus).setOnClickListener(v -> decreaseQuantity());
        findViewById(R.id.btnQuantityPlus).setOnClickListener(v -> increaseQuantity());

        // Date pickers
        findViewById(R.id.btnPickBorrowDate).setOnClickListener(v -> showDatePicker(true));
        findViewById(R.id.btnPickExpectedReturnDate).setOnClickListener(v -> showDatePicker(false));

        // Accessories selector
        findViewById(R.id.btnSelectAccessories).setOnClickListener(v -> showAccessoriesDialog());

        // Submit button
        findViewById(R.id.btnSubmitBorrowRequest).setOnClickListener(v -> submitBorrowRequest());
    }

    private void bindEquipmentData() {
        tvEquipmentName.setText(equipment.getName());
        tvEquipmentCategory.setText("Category: " + equipment.getCategoryName());
        tvAvailableStock.setText("Available: " + equipment.getAvailableQuantity());
        ivEquipmentImage.setImageResource(UiUtils.getEquipmentDrawableId(equipment.getImageName()));

        selectedAccessories = equipment.getDefaultAccessories() != null ? equipment.getDefaultAccessories() : "None";
        tvBorrowAccessories.setText(selectedAccessories);

        tvBorrowDate.setText(dateFormat.format(borrowCalendar.getTime()));
        tvExpectedReturnDate.setText(dateFormat.format(returnCalendar.getTime()));
    }

    private void bindUserData() {
        User user = sessionManager.getUser();
        tvBorrowerName.setText(user.getFullName() != null ? user.getFullName() : "Juan Delacruz");
    }

    private void increaseQuantity() {
        if (currentQuantity < equipment.getAvailableQuantity()) {
            currentQuantity++;
            tvQuantityCount.setText(String.valueOf(currentQuantity));
            updateSummary();
        } else {
            // View 2: System Feedback and Modal View (Figure 2 / FR-003)
            FeedbackDialogHelper.showStockLimitAlert(this, equipment.getName(), equipment.getAvailableQuantity());
        }
    }

    private void decreaseQuantity() {
        if (currentQuantity > 1) {
            currentQuantity--;
            tvQuantityCount.setText(String.valueOf(currentQuantity));
            updateSummary();
        }
    }

    private void updateSummary() {
        tvSummaryItems.setText(currentQuantity + (currentQuantity > 1 ? " items" : " item"));
        tvSummaryAccessories.setText(selectedAccessories.equalsIgnoreCase("None") ? "No accessory" : selectedAccessories);
        tvSummaryTotalQuantity.setText(currentQuantity + (currentQuantity > 1 ? " units" : " unit"));
    }

    private void showDatePicker(boolean isBorrowDate) {
        Calendar cal = isBorrowDate ? borrowCalendar : returnCalendar;
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    cal.set(year, month, dayOfMonth);
                    if (isBorrowDate) {
                        tvBorrowDate.setText(dateFormat.format(cal.getTime()));
                        // Ensure return date is after borrow date
                        if (returnCalendar.before(borrowCalendar)) {
                            returnCalendar.setTime(borrowCalendar.getTime());
                            returnCalendar.add(Calendar.DAY_OF_MONTH, 3);
                            tvExpectedReturnDate.setText(dateFormat.format(returnCalendar.getTime()));
                        }
                    } else {
                        tvExpectedReturnDate.setText(dateFormat.format(cal.getTime()));
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showAccessoriesDialog() {
        String[] options = {"None", "Standard Probes / Cables", "Complete Accessory Kit", "Power Supply Cord"};
        new AlertDialog.Builder(this)
                .setTitle("Select Accessories")
                .setItems(options, (dialog, which) -> {
                    selectedAccessories = options[which];
                    tvBorrowAccessories.setText(selectedAccessories);
                    updateSummary();
                })
                .show();
    }

    private void submitBorrowRequest() {
        if (equipment.getAvailableQuantity() <= 0) {
            FeedbackDialogHelper.showValidationError(this, "Equipment Unavailable",
                    "The selected item is currently out of stock. Please check back after laboratory returns are processed.");
            return;
        }

        if (currentQuantity > equipment.getAvailableQuantity()) {
            FeedbackDialogHelper.showStockLimitAlert(this, equipment.getName(), equipment.getAvailableQuantity());
            return;
        }

        if (returnCalendar.before(borrowCalendar)) {
            FeedbackDialogHelper.showValidationError(this, "Invalid Date Range",
                    "The expected return date cannot precede the borrowing start date.");
            return;
        }

        User user = sessionManager.getUser();
        String borrowDateStr = tvBorrowDate.getText().toString();
        String returnDateStr = tvExpectedReturnDate.getText().toString();
        String periodStr = borrowDateStr + " - " + returnDateStr;

        // System Feedback Confirmation Popup Modal
        FeedbackDialogHelper.showConfirmBorrowDialog(
                this,
                equipment.getName(),
                currentQuantity,
                periodStr,
                user.getFullName() != null ? user.getFullName() : "Juan Dela Cruz",
                () -> executeSubmission(user, borrowDateStr, returnDateStr)
        );
    }

    private void executeSubmission(User user, String borrowDateStr, String returnDateStr) {
        BorrowRepository.getInstance().submitBorrow(
                user.getUserId(),
                equipment,
                currentQuantity,
                borrowDateStr,
                returnDateStr,
                selectedCondition,
                selectedAccessories,
                new BorrowRepository.TransactionCallback() {
                    @Override
                    public void onSuccess(BorrowTransaction transaction) {
                        showSuccessAndRedirect(transaction);
                    }

                    @Override
                    public void onError(String message) {
                        FeedbackDialogHelper.showValidationError(BorrowActivity.this, "Submission Error", message);
                    }
                }
        );
    }

    private void showSuccessAndRedirect(BorrowTransaction transaction) {
        // System Feedback Success Modal
        FeedbackDialogHelper.showSuccessModal(
                this,
                "Request Submitted Successfully!",
                "Transaction ID: " + transaction.getTransactionId(),
                "Your loan request for " + equipment.getName() + " (Qty: " + currentQuantity + ") has been submitted with status PENDING. The Lab Custodian will review and approve your transaction.",
                () -> {
                    Intent intent = new Intent(BorrowActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
        );
    }
}
