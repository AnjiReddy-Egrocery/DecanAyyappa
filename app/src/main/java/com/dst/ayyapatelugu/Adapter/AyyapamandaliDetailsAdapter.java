package com.dst.ayyapatelugu.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dst.ayyapatelugu.Activity.AyyapaMandaliDetailsActivity;
import com.dst.ayyapatelugu.Fragment.AyyapaMandaliVivaranaFragment;
import com.dst.ayyapatelugu.Fragment.AyyapamandaliSandasamFragment;
import com.dst.ayyapatelugu.Fragment.AyyappaMandaliSwaiyaCharitraFragment;

public class AyyapamandaliDetailsAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;

    public AyyapamandaliDetailsAdapter(AyyapaMandaliDetailsActivity ayyapaMandaliDetailsActivity, @NonNull FragmentManager fm, int tabCount) {
        super(fm);
        myContext = ayyapaMandaliDetailsActivity;
        this.totalTabs = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AyyapaMandaliVivaranaFragment ayyapaMandaliVivaranaFragment = new AyyapaMandaliVivaranaFragment();
                return ayyapaMandaliVivaranaFragment;
            case 1:
                AyyappaMandaliSwaiyaCharitraFragment ayyappaMandaliSwaiyaCharitraFragment = new AyyappaMandaliSwaiyaCharitraFragment();
                return ayyappaMandaliSwaiyaCharitraFragment;
            case 2:
                AyyapamandaliSandasamFragment ayyapamandaliSandasamFragment = new AyyapamandaliSandasamFragment();
                return ayyapamandaliSandasamFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
