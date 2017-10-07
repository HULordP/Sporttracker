package com.example.olahbence.sporttracker.Result.Result.FragmentAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.olahbence.sporttracker.Result.Result.Fragments.AveragePacesFragment;
import com.example.olahbence.sporttracker.Result.Result.Fragments.DistanceFragment;

public class FragmentAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_PAGES = 2;

    public FragmentAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new DistanceFragment();
            case 1: return new AveragePacesFragment();
            default: return new DistanceFragment();
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }



}