package edu.bulsu.equipmentborrowing.ui.catalog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.bulsu.equipmentborrowing.R;
import edu.bulsu.equipmentborrowing.model.Equipment;
import edu.bulsu.equipmentborrowing.ui.borrow.BorrowActivity;
import edu.bulsu.equipmentborrowing.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class CatalogEquipmentAdapter extends RecyclerView.Adapter<CatalogEquipmentAdapter.ViewHolder> {

    private final Context context;
    private final List<Equipment> items = new ArrayList<>();

    public CatalogEquipmentAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<Equipment> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_catalog_equipment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Equipment item = items.get(position);
        holder.tvTitle.setText(item.getName());
        holder.tvCategory.setText("Category: " + item.getCategoryName());
        holder.tvAvailable.setText("Available: " + item.getAvailableQuantity());
        holder.ivImage.setImageResource(UiUtils.getEquipmentDrawableId(item.getImageName()));

        // All equipment items have functional Borrow buttons
        holder.btnBorrow.setOnClickListener(v -> {
            Intent intent = new Intent(context, BorrowActivity.class);
            intent.putExtra("equipment", item);
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BorrowActivity.class);
            intent.putExtra("equipment", item);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
        TextView tvCategory;
        TextView tvAvailable;
        Button btnBorrow;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCatalogImage);
            tvTitle = itemView.findViewById(R.id.tvCatalogTitle);
            tvCategory = itemView.findViewById(R.id.tvCatalogCategory);
            tvAvailable = itemView.findViewById(R.id.tvCatalogAvailable);
            btnBorrow = itemView.findViewById(R.id.btnCatalogBorrow);
        }
    }
}
