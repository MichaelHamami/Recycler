package com.hamami.recycler;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitleTabs = new ArrayList<>();

//    public ViewPagerAdapter(FragmentManager fm,ArrayList<Fragment> fragments,ArrayList<String> titleTabs) {
    public ViewPagerAdapter(FragmentManager fm) {

    super(fm);
//        mFragments = fragments;
//        mTitleTabs = titleTabs;
    }


    public void addFragment(Fragment fragment,String title)
    {
        mFragments.add(fragment);
        mTitleTabs.add(title);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleTabs.get(position);
    }
}
