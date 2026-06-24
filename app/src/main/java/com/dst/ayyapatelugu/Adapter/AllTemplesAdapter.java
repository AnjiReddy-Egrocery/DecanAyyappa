package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dst.ayyapatelugu.Activity.AnadanamActivity;
import com.dst.ayyapatelugu.Activity.ViewAllTemplesActivity;
import com.dst.ayyapatelugu.Fragment.AllAyyappaTemplesFragment;
import com.dst.ayyapatelugu.Fragment.MapsFragment;
import com.dst.ayyapatelugu.Fragment.TemplesFragment;
import com.dst.ayyapatelugu.Fragment.TemplesMapFragment;

public class AllTemplesAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;
    String templeId;
    String lat;
    String lng;
    public AllTemplesAdapter(ViewAllTemplesActivity viewAllTemplesActivity, FragmentManager supportFragmentManager, int tabCount, String templeId,
                             String lat,
                             String lng) {
        super(supportFragmentManager);
        myContext = viewAllTemplesActivity;
        this.totalTabs = tabCount;
        this.templeId = templeId;
        this.lat = lat;
        this.lng = lng;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TemplesMapFragment mapsFragment = new TemplesMapFragment();
                ViewAllTemplesActivity activity =
                        (ViewAllTemplesActivity) myContext;

                Bundle bundle = new Bundle();

                bundle.putBoolean(
                        "OPEN_TEMPLE",
                        activity.getIntent().getBooleanExtra("OPEN_TEMPLE", false)
                );

                bundle.putString(
                        "TEMPLE_ID",
                        activity.getIntent().getStringExtra("TEMPLE_ID")
                );

                bundle.putString(
                        "TEMPLE_LAT",
                        activity.getIntent().getStringExtra("TEMPLE_LAT")
                );

                bundle.putString(
                        "TEMPLE_LNG",
                        activity.getIntent().getStringExtra("TEMPLE_LNG")
                );

                mapsFragment.setArguments(bundle);

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
