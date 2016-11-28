package com.pefe.pefememo.app.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

/**
 * Created by dodoproject on 2016-11-07.
 */

public class CustomPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    ArrayList<Fragment> fragments = null;
    public CustomPagerAdapter(ArrayList<Fragment> fragments, FragmentManager fm) {
        super(fm);
        this.fragments = fragments;
    }
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
    @Override
    public int getCount() {
        return fragments.size();
    }
}
