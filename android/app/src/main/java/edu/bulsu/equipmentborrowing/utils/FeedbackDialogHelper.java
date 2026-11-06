package edu.bulsu.equipmentborrowing.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import edu.bulsu.equipmentborrowing.R;

public class FeedbackDialogHelper {

    public interface OnRemarksSubmittedListener {
        void onSubmit(String remarks);
    }

    /**
     * View 2: System Feedback and Modal View (Figure 2 in CPE 304L Final Exam document)
     * Triggered by FR-003 validation when quantity reaches available stock.
     */
    public static void showStockLimitAlert(Context context, String equipmentName, int maxAvailable) {
        Dialog dialog = createBaseDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_system_feedback, null);
        dialog.setContentView(view);

        ImageView ivIcon = view.findViewById(R.id.ivFeedbackIcon);
        TextView tvTitle = view.findViewById(R.id.tvFeedbackTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvFeedbackSubtitle);
        TextView tvMessage = view.findViewById(R.id.tvFeedbackMessage);
        TextView btnPositive = view.findViewById(R.id.btnFeedbackPositive);

        ivIcon.setImageResource(R.drawable.ic_feedback_warning);
        tvTitle.setText("Stock Limit Reached");
        tvSubtitle.setVisibility(View.VISIBLE);
        tvSubtitle.setText("Maximum Available: " + maxAvailable + " Units");
        tvMessage.setText("You have reached the maximum inventory count for " + equipmentName + ". The system does not permit borrow requests exceeding on-hand laboratory stock.");
        btnPositive.setText("Understood");

        btnPositive.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    /**
     * Validation Error Modal (FR-003)
     */
    public static void showValidationError(Context context, String title, String message) {
        Dialog dialog = createBaseDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_system_feedback, null);
        dialog.setContentView(view);

        ImageView ivIcon = view.findViewById(R.id.ivFeedbackIcon);
        TextView tvTitle = view.findViewById(R.id.tvFeedbackTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvFeedbackSubtitle);
        TextView tvMessage = view.findViewById(R.id.tvFeedbackMessage);
        TextView btnPositive = view.findViewById(R.id.btnFeedbackPositive);

        ivIcon.setImageResource(R.drawable.ic_feedback_error);
        tvTitle.setText(title);
        tvSubtitle.setVisibility(View.GONE);
        tvMessage.setText(message);
        btnPositive.setText("OK");

        btnPositive.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    /**
     * Success Status Change Modal (FR-004)
     */
    public static void showSuccessModal(Context context, String title, String subtitle, String message, Runnable onDismiss) {
        Dialog dialog = createBaseDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_system_feedback, null);
        dialog.setContentView(view);

        ImageView ivIcon = view.findViewById(R.id.ivFeedbackIcon);
        TextView tvTitle = view.findViewById(R.id.tvFeedbackTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvFeedbackSubtitle);
        TextView tvMessage = view.findViewById(R.id.tvFeedbackMessage);
        TextView btnPositive = view.findViewById(R.id.btnFeedbackPositive);

        ivIcon.setImageResource(R.drawable.ic_feedback_success);
        tvTitle.setText(title);
        if (subtitle != null && !subtitle.isEmpty()) {
            tvSubtitle.setVisibility(View.VISIBLE);
            tvSubtitle.setText(subtitle);
        } else {
            tvSubtitle.setVisibility(View.GONE);
        }
        tvMessage.setText(message);
        btnPositive.setText("View Details");

        btnPositive.setOnClickListener(v -> {
            dialog.dismiss();
            if (onDismiss != null) onDismiss.run();
        });
        dialog.show();
    }

    /**
     * Borrow Confirmation Popup Modal before submitting
     */
    public static void showConfirmBorrowDialog(Context context, String itemName, int quantity,
                                              String period, String borrower, Runnable onConfirm) {
        Dialog dialog = createBaseDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_borrow, null);
        dialog.setContentView(view);

        TextView tvItem = view.findViewById(R.id.tvConfirmItemName);
        TextView tvQty = view.findViewById(R.id.tvConfirmQuantity);
        TextView tvPeriod = view.findViewById(R.id.tvConfirmPeriod);
        TextView tvBorrower = view.findViewById(R.id.tvConfirmBorrower);

        tvItem.setText(itemName);
        tvQty.setText(quantity + " unit" + (quantity > 1 ? "s" : ""));
        tvPeriod.setText(period);
        tvBorrower.setText(borrower);

        view.findViewById(R.id.btnConfirmCancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnConfirmSubmit).setOnClickListener(v -> {
            dialog.dismiss();
            if (onConfirm != null) onConfirm.run();
        });

        dialog.show();
    }

    /**
     * Administrator Remarks / Rejection Dialog
     */
    public static void showAdminRemarksDialog(Context context, String title, String trxId,
                                             boolean isReject, OnRemarksSubmittedListener listener) {
        Dialog dialog = createBaseDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_admin_remarks, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvAdminDialogTitle);
        TextView tvTrx = view.findViewById(R.id.tvAdminDialogTrx);
        EditText etRemarks = view.findViewById(R.id.etAdminRemarksInput);
        ImageView ivIcon = view.findViewById(R.id.ivAdminDialogIcon);

        tvTitle.setText(title);
        tvTrx.setText("Transaction ID: " + trxId);

        if (isReject) {
            ivIcon.setImageResource(R.drawable.ic_feedback_error);
            etRemarks.setHint("Please state reason for rejection (e.g. Schedule conflict, missing prerequisite).");
        } else {
            ivIcon.setImageResource(R.drawable.ic_feedback_info);
            etRemarks.setHint("Optional remarks or instructions for the student.");
        }

        view.findViewById(R.id.btnAdminDialogCancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnAdminDialogConfirm).setOnClickListener(v -> {
            String remarks = etRemarks.getText().toString().trim();
            if (isReject && remarks.isEmpty()) {
                etRemarks.setError("Remarks are required when rejecting a request.");
                return;
            }
            dialog.dismiss();
            if (listener != null) listener.onSubmit(remarks);
        });

        dialog.show();
    }

    /**
     * Notifications Modal when tapping the Notification bell
     */
    public static void showNotificationsModal(Context context) {
        Dialog dialog = createBaseDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_notifications, null);
        dialog.setContentView(view);

        view.findViewById(R.id.btnCloseNotifications).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnDismissNotifications).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private static Dialog createBaseDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(true);
        return dialog;
    }
}
