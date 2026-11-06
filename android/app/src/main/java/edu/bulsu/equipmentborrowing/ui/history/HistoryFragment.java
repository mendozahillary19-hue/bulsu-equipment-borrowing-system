package edu.bulsu.equipmentborrowing.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.bulsu.equipmentborrowing.R;
import edu.bulsu.equipmentborrowing.model.BorrowTransaction;
import edu.bulsu.equipmentborrowing.model.User;
import edu.bulsu.equipmentborrowing.repository.BorrowRepository;
import edu.bulsu.equipmentborrowing.utils.SessionManager;

import java.util.List;

public class HistoryFragment extends Fragment {

    private TextView tabAll, tabPending, tabApproved, tabReturned, tabRejected;
    private TextView tvEmptyNotice;
    private RecyclerView rvHistory;
    private BorrowHistoryAdapter adapter;
    private SessionManager sessionManager;

    private String currentFilter = "ALL";

    public void setSelectedFilter(String filter) {
        this.currentFilter = filter != null ? filter.toUpperCase() : "ALL";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        sessionManager = new SessionManager(requireContext());

        tabAll = view.findViewById(R.id.tabHistAll);
        tabPending = view.findViewById(R.id.tabHistPending);
        tabApproved = view.findViewById(R.id.tabHistApproved);
        tabReturned = view.findViewById(R.id.tabHistReturned);
        tabRejected = view.findViewById(R.id.tabHistRejected);
        tvEmptyNotice = view.findViewById(R.id.tvHistoryEmpty);
        rvHistory = view.findViewById(R.id.rvBorrowHistory);

        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BorrowHistoryAdapter(requireContext());
        rvHistory.setAdapter(adapter);

        tabAll.setOnClickListener(v -> setFilter("ALL"));
        tabPending.setOnClickListener(v -> setFilter("PENDING"));
        tabApproved.setOnClickListener(v -> setFilter("APPROVED"));
        tabReturned.setOnClickListener(v -> setFilter("RETURNED"));
        tabRejected.setOnClickListener(v -> setFilter("REJECTED"));

        updateTabUi();
        loadHistory();

        return view;
    }

    private void setFilter(String filter) {
        this.currentFilter = filter;
        updateTabUi();
        loadHistory();
    }

    private void updateTabUi() {
        if (!isAdded()) return;

        int selectedBg = R.drawable.bg_category_chip_selected;
        int unselectedBg = R.drawable.bg_category_chip;

        int selectedColor = ContextCompat.getColor(requireContext(), R.color.tab_selected_text);
        int unselectedColor = ContextCompat.getColor(requireContext(), R.color.tab_unselected_text);

        // Reset all
        tabAll.setBackgroundResource(unselectedBg); tabAll.setTextColor(unselectedColor);
        tabPending.setBackgroundResource(unselectedBg); tabPending.setTextColor(unselectedColor);
        tabApproved.setBackgroundResource(unselectedBg); tabApproved.setTextColor(unselectedColor);
        tabReturned.setBackgroundResource(unselectedBg); tabReturned.setTextColor(unselectedColor);
        tabRejected.setBackgroundResource(unselectedBg); tabRejected.setTextColor(unselectedColor);

        switch (currentFilter.toUpperCase()) {
            case "PENDING":
                tabPending.setBackgroundResource(selectedBg);
                tabPending.setTextColor(selectedColor);
                break;
            case "APPROVED":
                tabApproved.setBackgroundResource(selectedBg);
                tabApproved.setTextColor(selectedColor);
                break;
            case "RETURNED":
                tabReturned.setBackgroundResource(selectedBg);
                tabReturned.setTextColor(selectedColor);
                break;
            case "REJECTED":
                tabRejected.setBackgroundResource(selectedBg);
                tabRejected.setTextColor(selectedColor);
                break;
            case "ALL":
            default:
                tabAll.setBackgroundResource(selectedBg);
                tabAll.setTextColor(selectedColor);
                break;
        }
    }

    private void loadHistory() {
        User user = sessionManager.getUser();
        BorrowRepository.getInstance().getUserTransactions(user.getUserId(), currentFilter,
                new BorrowRepository.TransactionListCallback() {
                    @Override
                    public void onSuccess(List<BorrowTransaction> list) {
                        adapter.setTransactions(list);
                        if (list == null || list.isEmpty()) {
                            tvEmptyNotice.setVisibility(View.VISIBLE);
                        } else {
                            tvEmptyNotice.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        tvEmptyNotice.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTabUi();
        loadHistory();
    }
}
