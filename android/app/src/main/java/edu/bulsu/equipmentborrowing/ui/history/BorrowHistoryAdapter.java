package edu.bulsu.equipmentborrowing.ui.history;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.bulsu.equipmentborrowing.R;
import edu.bulsu.equipmentborrowing.model.BorrowTransaction;
import edu.bulsu.equipmentborrowing.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class BorrowHistoryAdapter extends RecyclerView.Adapter<BorrowHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<BorrowTransaction> transactions = new ArrayList<>();

    public BorrowHistoryAdapter(Context context) {
        this.context = context;
    }

    public void setTransactions(List<BorrowTransaction> items) {
        this.transactions.clear();
        if (items != null) {
            this.transactions.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BorrowTransaction item = transactions.get(position);
        holder.tvEquipmentName.setText(item.getEquipmentName());
        holder.tvQuantity.setText("Qty: " + item.getQuantity());

        String dateRange = item.getBorrowDate() + " - " + item.getExpectedReturnDate();
        holder.tvDateRange.setText(dateRange);

        holder.tvStatusBadge.setText(item.getStatus());
        holder.tvStatusBadge.setBackgroundResource(UiUtils.getStatusBgDrawable(item.getStatus()));
        holder.tvStatusBadge.setTextColor(UiUtils.getStatusTextColor(context, item.getStatus()));

        holder.ivImage.setImageResource(UiUtils.getEquipmentDrawableId(item.getImageName()));

        // Entire request card is clickable
        View.OnClickListener clickListener = v -> {
            Intent intent = new Intent(context, RequestDetailActivity.class);
            intent.putExtra("transaction", item);
            context.startActivity(intent);
        };

        holder.itemView.setOnClickListener(clickListener);
        holder.ivChevron.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvEquipmentName;
        TextView tvQuantity;
        TextView tvDateRange;
        TextView tvStatusBadge;
        ImageView ivChevron;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivHistoryImage);
            tvEquipmentName = itemView.findViewById(R.id.tvHistoryEquipmentName);
            tvQuantity = itemView.findViewById(R.id.tvHistoryQuantity);
            tvDateRange = itemView.findViewById(R.id.tvHistoryDateRange);
            tvStatusBadge = itemView.findViewById(R.id.tvHistoryStatusBadge);
            ivChevron = itemView.findViewById(R.id.ivHistoryChevron);
        }
    }
}
