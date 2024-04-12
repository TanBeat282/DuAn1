package com.example.duan1_nhom8.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.duan1_nhom8.fragments.DaHuyFragment;
import com.example.duan1_nhom8.fragments.DaMuaFragment;
import com.example.duan1_nhom8.fragments.DangMuaFragment;

public class Fragment_DonHangAdapter extends FragmentStateAdapter {
    public Fragment_DonHangAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = DangMuaFragment.newInstance();
        } else if (position == 1) {
            fragment = DaMuaFragment.newInstance();
        } else {
            fragment = DaHuyFragment.newInstance();
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
