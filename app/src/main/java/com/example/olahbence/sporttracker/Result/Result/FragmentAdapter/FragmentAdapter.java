package com.example.olahbence.sporttracker.Result.Result.FragmentAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.olahbence.sporttracker.Result.Result.Fragments.AveragePaces.AveragePacesFragment;
import com.example.olahbence.sporttracker.Result.Result.Fragments.DistanceFragment;
import com.example.olahbence.sporttracker.Result.Result.Fragments.Post.PostsFragment;

public class FragmentAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 3;

    public FragmentAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DistanceFragment();
            case 1:
                return new AveragePacesFragment();
            case 2:
                return new PostsFragment();
            default:
                return new DistanceFragment();
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Map";
            case 1:
                return "Average paces";
            case 2:
                return "Posts";
            default:
                return "Map";
        }
    }




}