package edu.bulsu.equipmentborrowing.ui.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.bulsu.equipmentborrowing.R;
import edu.bulsu.equipmentborrowing.model.BorrowTransaction;
import edu.bulsu.equipmentborrowing.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class AdminTransactionAdapter extends RecyclerView.Adapter<AdminTransactionAdapter.ViewHolder> {

    public interface AdminActionListener {
        void onApprove(BorrowTransaction tx);
        void onReject(BorrowTransaction tx);
    }

    private final Context context;
    private final List<BorrowTransaction> transactions = new ArrayList<>();
    private final AdminActionListener listener;

    public AdminTransactionAdapter(Context context, AdminActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setTransactions(List<BorrowTransaction> list) {
        this.transactions.clear();
        if (list != null) {
            this.transactions.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BorrowTransaction tx = transactions.get(position);

        holder.tvTxId.setText(tx.getTransactionId());
        holder.tvStatus.setText(tx.getStatus());
        holder.tvStatus.setBackgroundResource(UiUtils.getStatusBgDrawable(tx.getStatus()));
        holder.tvStatus.setTextColor(UiUtils.getStatusTextColor(context, tx.getStatus()));

        holder.tvItemDetails.setText(tx.getEquipmentName() + " (Qty: " + tx.getQuantity() + ")");
        holder.tvBorrower.setText("Borrower: " + tx.getBorrowerName() + " (" + tx.getStudentId() + ")");
        holder.tvDates.setText("Period: " + tx.getBorrowDate() + " - " + tx.getExpectedReturnDate());

        if ("PENDING".equalsIgnoreCase(tx.getStatus())) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnApprove.setOnClickListener(v -> listener.onApprove(tx));
            holder.btnReject.setOnClickListener(v -> listener.onReject(tx));
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTxId, tvStatus, tvItemDetails, tvBorrower, tvDates;
        View layoutActions;
        Button btnApprove, btnReject;

        ViewHolder(View itemView) {
            super(itemView);
            tvTxId = itemView.findViewById(R.id.tvAdminTxId);
            tvStatus = itemView.findViewById(R.id.tvAdminStatus);
            tvItemDetails = itemView.findViewById(R.id.tvAdminItemDetails);
            tvBorrower = itemView.findViewById(R.id.tvAdminBorrower);
            tvDates = itemView.findViewById(R.id.tvAdminDates);
            layoutActions = itemView.findViewById(R.id.layoutAdminActions);
            btnApprove = itemView.findViewById(R.id.btnAdminApprove);
            btnReject = itemView.findViewById(R.id.btnAdminReject);
        }
    }
}
