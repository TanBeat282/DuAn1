package com.example.duan1_nhom8.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.Fragment_DonHangAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DonHangFragment extends Fragment {
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private DonHangViewModel mViewModel;

    public static DonHangFragment newInstance() {
        return new DonHangFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_don_hang, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DonHangViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager2 = view.findViewById(R.id.Chi_ViewPager2);
        tabLayout = view.findViewById(R.id.tabLayout);
        Fragment_DonHangAdapter fragment_donHangAdapter = new Fragment_DonHangAdapter(getActivity());
        viewPager2.setAdapter(fragment_donHangAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Đang mua hàng");
                } else if (position == 1) {
                    tab.setText("Lich sử mua hàng");
                } else {
                    tab.setText("Đơn hàng bị hủy");
                }
            }
        }).attach();
    }
}