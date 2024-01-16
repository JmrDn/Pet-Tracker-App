package com.example.pettrackerfersoncapstone.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pettrackerfersoncapstone.HomeTabFragment.HeartRateFragment;
import com.example.pettrackerfersoncapstone.HomeTabFragment.MapFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
            default:
                return new HeartRateFragment();
            case 1:
                return new MapFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
