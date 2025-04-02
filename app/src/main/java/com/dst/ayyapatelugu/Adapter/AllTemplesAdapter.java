package com.dst.ayyapatelugu.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dst.ayyapatelugu.Activity.ViewAllTemplesActivity;
import com.dst.ayyapatelugu.Fragment.AllAyyappaTemplesFragment;
import com.dst.ayyapatelugu.Fragment.MapsFragment;
import com.dst.ayyapatelugu.Fragment.TemplesFragment;
import com.dst.ayyapatelugu.Fragment.TemplesMapFragment;

public class AllTemplesAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;
    public AllTemplesAdapter(ViewAllTemplesActivity viewAllTemplesActivity, FragmentManager supportFragmentManager, int tabCount) {
        super(supportFragmentManager);
        myContext = viewAllTemplesActivity;
        this.totalTabs = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TemplesMapFragment mapsFragment = new TemplesMapFragment();
                return mapsFragment;
            case 1:
                TemplesFragment templesFragment = new TemplesFragment();
                return templesFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
