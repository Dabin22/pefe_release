package com.pefe.pefememo.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

/**
 * Created by dodoproject on 2016-11-07.
 */

public class PagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    ArrayList<Fragment> fragments = null;
    public PagerAdapter(ArrayList<Fragment> fragments, FragmentManager fm) {
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
