package edu.bulsu.equipmentborrowing.ui.home;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import edu.bulsu.equipmentborrowing.R;
import edu.bulsu.equipmentborrowing.model.DashboardStats;
import edu.bulsu.equipmentborrowing.model.Equipment;
import edu.bulsu.equipmentborrowing.model.User;
import edu.bulsu.equipmentborrowing.network.ApiClient;
import edu.bulsu.equipmentborrowing.repository.BorrowRepository;
import edu.bulsu.equipmentborrowing.repository.EquipmentRepository;
import edu.bulsu.equipmentborrowing.ui.main.MainActivity;
import edu.bulsu.equipmentborrowing.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvWelcomeGreeting, tvAvailableCount, tvMyRequestsCount;
    private EditText etHomeSearch;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvFeatured;
    private FeaturedEquipmentAdapter adapter;
    private SessionManager sessionManager;
    private ImageView ivCatAll, ivCatTools, ivCatElectronics, ivCatTesting, ivCatOthers;
    private int selectedCategory = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(requireContext());

        tvWelcomeGreeting = view.findViewById(R.id.tvWelcomeGreeting);
        tvAvailableCount = view.findViewById(R.id.tvAvailableCount);
        tvMyRequestsCount = view.findViewById(R.id.tvMyRequestsCount);
        etHomeSearch = view.findViewById(R.id.etHomeSearch);
        swipeRefresh = view.findViewById(R.id.swipeRefreshHome);
        rvFeatured = view.findViewById(R.id.rvFeaturedEquipment);

        ivCatAll = view.findViewById(R.id.ivCatAll);
        ivCatTools = view.findViewById(R.id.ivCatTools);
        ivCatElectronics = view.findViewById(R.id.ivCatElectronics);
        ivCatTesting = view.findViewById(R.id.ivCatTesting);
        ivCatOthers = view.findViewById(R.id.ivCatOthers);

        rvFeatured.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new FeaturedEquipmentAdapter(requireContext());
        rvFeatured.setAdapter(adapter);

        // Set personalized greeting
        User user = sessionManager.getUser();
        String firstName = user.getFullName() != null ? user.getFullName().split(" ")[0] : "Student";
        tvWelcomeGreeting.setText("Welcome,\n" + firstName + "!");

        setupCategoryClickListeners(view);
        setupSearchListener();

        view.findViewById(R.id.btnNotification).setOnClickListener(v ->
                edu.bulsu.equipmentborrowing.utils.FeedbackDialogHelper.showNotificationsModal(requireContext()));

        swipeRefresh.setOnRefreshListener(this::loadData);

        view.findViewById(R.id.cardAvailableStats).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectTab(MainActivity.TAB_CATALOG);
            }
        });

        view.findViewById(R.id.cardMyRequestsStats).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToHistoryWithFilter("Pending");
            }
        });

        view.findViewById(R.id.tvSeeAllCategories).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToCatalogWithCategory(selectedCategory);
            }
        });

        loadData();

        return view;
    }

    private void setupCategoryClickListeners(View view) {
        view.findViewById(R.id.catAll).setOnClickListener(v -> selectCategory(0));
        view.findViewById(R.id.catTools).setOnClickListener(v -> selectCategory(1));
        view.findViewById(R.id.catElectronics).setOnClickListener(v -> selectCategory(2));
        view.findViewById(R.id.catTesting).setOnClickListener(v -> selectCategory(3));
        view.findViewById(R.id.catOthers).setOnClickListener(v -> selectCategory(4));
    }

    private void selectCategory(int categoryId) {
        this.selectedCategory = categoryId;
        resetCategoryIcons();

        int goldBg = R.drawable.bg_category_active_gold;
        int whiteColor = ContextCompat.getColor(requireContext(), R.color.text_white);

        if (categoryId == 0) {
            ivCatAll.setBackgroundResource(goldBg);
            ivCatAll.setColorFilter(whiteColor);
        } else if (categoryId == 1) {
            ivCatTools.setBackgroundResource(goldBg);
            ivCatTools.setColorFilter(whiteColor);
        } else if (categoryId == 2) {
            ivCatElectronics.setBackgroundResource(goldBg);
            ivCatElectronics.setColorFilter(whiteColor);
        } else if (categoryId == 3) {
            ivCatTesting.setBackgroundResource(goldBg);
            ivCatTesting.setColorFilter(whiteColor);
        } else if (categoryId == 4) {
            ivCatOthers.setBackgroundResource(goldBg);
            ivCatOthers.setColorFilter(whiteColor);
        }

        List<Equipment> featured = EquipmentRepository.getInstance().getFeaturedForCategory(categoryId);
        adapter.setItems(featured);
    }

    private void resetCategoryIcons() {
        int inactiveBg = R.drawable.bg_category_inactive;
        int darkColor = ContextCompat.getColor(requireContext(), R.color.text_primary);

        ivCatAll.setBackgroundResource(inactiveBg);
        ivCatAll.setColorFilter(darkColor);

        ivCatTools.setBackgroundResource(inactiveBg);
        ivCatTools.setColorFilter(darkColor);

        ivCatElectronics.setBackgroundResource(inactiveBg);
        ivCatElectronics.setColorFilter(darkColor);

        ivCatTesting.setBackgroundResource(inactiveBg);
        ivCatTesting.setColorFilter(darkColor);

        ivCatOthers.setBackgroundResource(inactiveBg);
        ivCatOthers.setColorFilter(darkColor);
    }

    private void setupSearchListener() {
        etHomeSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String query = etHomeSearch.getText().toString().trim();
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToCatalogWithSearch(query);
                }
                return true;
            }
            return false;
        });
    }

    private void loadData() {
        swipeRefresh.setRefreshing(true);

        User user = sessionManager.getUser();

        // 1. Load dynamic dashboard counts
        ApiClient.getService().getDashboardStats(user.getUserId()).enqueue(new Callback<DashboardStats>() {
            @Override
            public void onResponse(Call<DashboardStats> call, Response<DashboardStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvAvailableCount.setText(String.valueOf(response.body().getAvailableEquipmentCount()));
                    tvMyRequestsCount.setText(String.valueOf(response.body().getMyRequestsCount()));
                } else {
                    loadLocalStats();
                }
            }

            @Override
            public void onFailure(Call<DashboardStats> call, Throwable t) {
                loadLocalStats();
            }
        });

        // 2. Initial state: select category 0 (ALL)
        selectCategory(0);
        swipeRefresh.setRefreshing(false);
    }

    private void loadLocalStats() {
        tvAvailableCount.setText(String.valueOf(EquipmentRepository.getInstance().getTotalAvailableCountLocal()));
        tvMyRequestsCount.setText(String.valueOf(BorrowRepository.getInstance().getPendingCountLocal()));
    }
}
