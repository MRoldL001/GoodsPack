package com.mroldl001.goodspack;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        bottomNav = findViewById(R.id.bottom_navigation);

        viewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return new HomeFragment();
                } else if (position == 1) {
                    return new DashboardFragment();
                } else if (position == 2) {
                    return new NotificationsFragment();
                } else {
                    return new HomeFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3; // 3个 Fragment
            }
        });
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // 同步底栏状态
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    bottomNav.setSelectedItemId(R.id.nav_basket);
                } else if (position == 1) {
                    bottomNav.setSelectedItemId(R.id.nav_dashboard);
                } else if (position == 2) {
                    bottomNav.setSelectedItemId(R.id.nav_notifications);
                }
            }
        });
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_basket) {
                        viewPager.setCurrentItem(0, true);
                    } else if (itemId == R.id.nav_dashboard) {
                        viewPager.setCurrentItem(1, true);
                    } else if (itemId == R.id.nav_notifications) {
                        viewPager.setCurrentItem(2, true);
                    }
                    return true;
                }
            };
}
