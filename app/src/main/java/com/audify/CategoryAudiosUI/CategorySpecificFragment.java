package com.audify.CategoryAudiosUI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.audify.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class CategorySpecificFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    CategoryAudiosFragment categoryAudiosFragment = new CategoryAudiosFragment();
    CategoryCreatorsFragment categoryCreatorsFragment = new CategoryCreatorsFragment();

    String categoryName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_specific, container, false);

        Bundle bundle = getArguments();
        categoryName = bundle.getString("categoryName");

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.view_pager);

        categoryAudiosFragment = new CategoryAudiosFragment();
        categoryCreatorsFragment = new CategoryCreatorsFragment();

        categoryAudiosFragment.setArguments(bundle);
        categoryCreatorsFragment.setArguments(bundle);

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(),0);
        viewPagerAdapter.addFragment(categoryAudiosFragment,"Audios");
        viewPagerAdapter.addFragment(categoryCreatorsFragment,"Experts");
        viewPager.setAdapter(viewPagerAdapter);

        return view;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title){
            fragmentList.add(fragment);
            fragmentTitle.add(title);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}