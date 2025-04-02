package com.dst.ayyapatelugu.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dst.ayyapatelugu.Activity.ViewAllAyyappaTemplesActivity;
import com.dst.ayyapatelugu.Fragment.AllAyyappaTemplesFragment;
import com.dst.ayyapatelugu.Fragment.MapsFragment;
import com.dst.ayyapatelugu.Fragment.SandasamFragment;
import com.dst.ayyapatelugu.Fragment.SwiyaCharitraFragment;
import com.dst.ayyapatelugu.Fragment.VivaranaFragment;

public class AllAyyappaTemplesAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;


    public AllAyyappaTemplesAdapter(ViewAllAyyappaTemplesActivity viewAllAyyappaTemplesActivity, FragmentManager supportFragmentManager, int tabCount) {
        super(supportFragmentManager);
        myContext = viewAllAyyappaTemplesActivity;
        this.totalTabs = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MapsFragment mapsFragment = new MapsFragment();
                return mapsFragment;
            case 1:
                AllAyyappaTemplesFragment allAyyappaTemplesFragment = new AllAyyappaTemplesFragment();
                return allAyyappaTemplesFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
