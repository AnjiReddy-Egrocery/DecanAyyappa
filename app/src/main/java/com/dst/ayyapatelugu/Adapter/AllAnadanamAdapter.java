package com.dst.ayyapatelugu.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dst.ayyapatelugu.Activity.AnadanamActivity;
import com.dst.ayyapatelugu.Activity.ViewAllTemplesActivity;
import com.dst.ayyapatelugu.Fragment.AnadanamMapFragment;
import com.dst.ayyapatelugu.Fragment.AnadanamTemplesFragment;
import com.dst.ayyapatelugu.Fragment.TemplesFragment;
import com.dst.ayyapatelugu.Fragment.TemplesMapFragment;

public class AllAnadanamAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;

  public AllAnadanamAdapter(AnadanamActivity anadanamActivity, FragmentManager supportFragmentManager, int tabCount) {
        super(supportFragmentManager);
        myContext = anadanamActivity;
        this.totalTabs = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AnadanamMapFragment mapsFragment = new AnadanamMapFragment();
                return mapsFragment;
            case 1:
                AnadanamTemplesFragment templesFragment = new AnadanamTemplesFragment();
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
