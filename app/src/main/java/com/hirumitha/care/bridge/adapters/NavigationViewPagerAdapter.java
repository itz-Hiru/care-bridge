package com.hirumitha.care.bridge.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hirumitha.care.bridge.fragments.HomeFragment;
import com.hirumitha.care.bridge.fragments.NotificationsFragment;
import com.hirumitha.care.bridge.fragments.SettingsFragment;

public class NavigationViewPagerAdapter extends FragmentStateAdapter {

    public NavigationViewPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new NotificationsFragment();
            case 2:
                return new SettingsFragment();
            case 0:
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}