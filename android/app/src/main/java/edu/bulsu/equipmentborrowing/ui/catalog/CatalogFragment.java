package edu.bulsu.equipmentborrowing.ui.catalog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.bulsu.equipmentborrowing.R;
import edu.bulsu.equipmentborrowing.model.Equipment;
import edu.bulsu.equipmentborrowing.repository.EquipmentRepository;
import edu.bulsu.equipmentborrowing.ui.main.MainActivity;

import java.util.List;

public class CatalogFragment extends Fragment {

    private EditText etSearch;
    private TextView tvEmptyNotice;
    private TextView tabAll, tabTools, tabElectronics, tabTesting, tabOthers;
    private RecyclerView rvEquipment;
    private CatalogEquipmentAdapter adapter;

    private int selectedCategoryId = 0; // 0 = ALL
    private String currentSearchQuery = "";

    public void setSelectedCategoryInitial(int categoryId) {
        this.selectedCategoryId = categoryId;
    }

    public void setSearchQueryInitial(String query) {
        this.currentSearchQuery = query != null ? query : "";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        etSearch = view.findViewById(R.id.etCatalogSearch);
        tvEmptyNotice = view.findViewById(R.id.tvCatalogEmpty);
        rvEquipment = view.findViewById(R.id.rvCatalogEquipment);

        tabAll = view.findViewById(R.id.tabAll);
        tabTools = view.findViewById(R.id.tabTools);
        tabElectronics = view.findViewById(R.id.tabElectronics);
        tabTesting = view.findViewById(R.id.tabTesting);
        tabOthers = view.findViewById(R.id.tabOthers);

        rvEquipment.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CatalogEquipmentAdapter(requireContext());
        rvEquipment.setAdapter(adapter);

        // Back button returns to Home
        view.findViewById(R.id.btnCatalogBack).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectTab(MainActivity.TAB_HOME);
            }
        });

        // Tab click listeners
        tabAll.setOnClickListener(v -> selectCategory(0));
        tabTools.setOnClickListener(v -> selectCategory(1));
        tabElectronics.setOnClickListener(v -> selectCategory(2));
        tabTesting.setOnClickListener(v -> selectCategory(3));
        tabOthers.setOnClickListener(v -> selectCategory(4));

        // Real-time immediate search filtering
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                fetchEquipment();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        view.findViewById(R.id.btnCatalogClearSearch).setOnClickListener(v -> {
            etSearch.setText("");
        });

        if (!currentSearchQuery.isEmpty()) {
            etSearch.setText(currentSearchQuery);
        }

        updateTabUi();
        fetchEquipment();

        return view;
    }

    private void selectCategory(int categoryId) {
        this.selectedCategoryId = categoryId;
        updateTabUi();
        fetchEquipment();
    }

    private void updateTabUi() {
        if (!isAdded()) return;

        int selectedBg = R.drawable.bg_category_chip_selected;
        int unselectedBg = R.drawable.bg_category_chip;

        int selectedColor = ContextCompat.getColor(requireContext(), R.color.tab_selected_text);
        int unselectedColor = ContextCompat.getColor(requireContext(), R.color.tab_unselected_text);

        // Reset all
        tabAll.setBackgroundResource(unselectedBg); tabAll.setTextColor(unselectedColor);
        tabTools.setBackgroundResource(unselectedBg); tabTools.setTextColor(unselectedColor);
        tabElectronics.setBackgroundResource(unselectedBg); tabElectronics.setTextColor(unselectedColor);
        tabTesting.setBackgroundResource(unselectedBg); tabTesting.setTextColor(unselectedColor);
        tabOthers.setBackgroundResource(unselectedBg); tabOthers.setTextColor(unselectedColor);

        // Highlight selected
        switch (selectedCategoryId) {
            case 1:
                tabTools.setBackgroundResource(selectedBg);
                tabTools.setTextColor(selectedColor);
                break;
            case 2:
                tabElectronics.setBackgroundResource(selectedBg);
                tabElectronics.setTextColor(selectedColor);
                break;
            case 3:
                tabTesting.setBackgroundResource(selectedBg);
                tabTesting.setTextColor(selectedColor);
                break;
            case 4:
                tabOthers.setBackgroundResource(selectedBg);
                tabOthers.setTextColor(selectedColor);
                break;
            case 0:
            default:
                tabAll.setBackgroundResource(selectedBg);
                tabAll.setTextColor(selectedColor);
                break;
        }
    }

    private void fetchEquipment() {
        EquipmentRepository.getInstance().getEquipment(currentSearchQuery, selectedCategoryId,
                new EquipmentRepository.DataCallback<List<Equipment>>() {
                    @Override
                    public void onSuccess(List<Equipment> data) {
                        adapter.setItems(data);
                        if (data == null || data.isEmpty()) {
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
        fetchEquipment();
    }
}
