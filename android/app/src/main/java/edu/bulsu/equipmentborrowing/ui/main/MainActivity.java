package edu.bulsu.equipmentborrowing.ui.main;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import edu.bulsu.equipmentborrowing.R;
import edu.bulsu.equipmentborrowing.ui.catalog.CatalogFragment;
import edu.bulsu.equipmentborrowing.ui.history.HistoryFragment;
import edu.bulsu.equipmentborrowing.ui.home.HomeFragment;
import edu.bulsu.equipmentborrowing.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    public static final int TAB_HOME = 0;
    public static final int TAB_CATALOG = 1;
    public static final int TAB_HISTORY = 2;
    public static final int TAB_PROFILE = 3;

    private int currentTab = TAB_HOME;

    private ImageView ivNavHome, ivNavCatalog, ivNavHistory, ivNavProfile;
    private final Fragment homeFragment = new HomeFragment();
    private final CatalogFragment catalogFragment = new CatalogFragment();
    private final HistoryFragment historyFragment = new HistoryFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivNavHome = findViewById(R.id.ivNavHome);
        ivNavCatalog = findViewById(R.id.ivNavCatalog);
        ivNavHistory = findViewById(R.id.ivNavHistory);
        ivNavProfile = findViewById(R.id.ivNavProfile);

        findViewById(R.id.navHome).setOnClickListener(v -> selectTab(TAB_HOME));
        findViewById(R.id.navCatalog).setOnClickListener(v -> selectTab(TAB_CATALOG));
        findViewById(R.id.navHistory).setOnClickListener(v -> selectTab(TAB_HISTORY));
        findViewById(R.id.navProfile).setOnClickListener(v -> selectTab(TAB_PROFILE));

        // Initial tab
        selectTab(TAB_HOME);
    }

    public void selectTab(int tabIndex) {
        currentTab = tabIndex;
        Fragment selectedFragment;

        int activeColor = ContextCompat.getColor(this, R.color.primary_blue);
        int inactiveColor = ContextCompat.getColor(this, R.color.text_primary);

        ivNavHome.setColorFilter(inactiveColor);
        ivNavCatalog.setColorFilter(inactiveColor);
        ivNavHistory.setColorFilter(inactiveColor);
        ivNavProfile.setColorFilter(inactiveColor);

        switch (tabIndex) {
            case TAB_CATALOG:
                selectedFragment = catalogFragment;
                ivNavCatalog.setColorFilter(activeColor);
                break;
            case TAB_HISTORY:
                selectedFragment = historyFragment;
                ivNavHistory.setColorFilter(activeColor);
                break;
            case TAB_PROFILE:
                selectedFragment = profileFragment;
                ivNavProfile.setColorFilter(activeColor);
                break;
            case TAB_HOME:
            default:
                selectedFragment = homeFragment;
                ivNavHome.setColorFilter(activeColor);
                break;
        }

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragmentContainer, selectedFragment)
                .commit();
    }

    public void navigateToCatalogWithCategory(int categoryId) {
        catalogFragment.setSelectedCategoryInitial(categoryId);
        selectTab(TAB_CATALOG);
    }

    public void navigateToCatalogWithSearch(String query) {
        catalogFragment.setSearchQueryInitial(query);
        selectTab(TAB_CATALOG);
    }

    public void navigateToHistoryWithFilter(String filter) {
        historyFragment.setSelectedFilter(filter);
        selectTab(TAB_HISTORY);
    }
}
