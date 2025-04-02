package com.dst.ayyapatelugu.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dst.ayyapatelugu.Activity.GuruSwamiDetailsActivity;
import com.dst.ayyapatelugu.Fragment.SandasamFragment;
import com.dst.ayyapatelugu.Fragment.SwiyaCharitraFragment;
import com.dst.ayyapatelugu.Fragment.VivaranaFragment;

public class GuruSwamiDetailsAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;

    public GuruSwamiDetailsAdapter(GuruSwamiDetailsActivity guruSwamiDetailsActivity, @NonNull FragmentManager fm, int tabCount) {
        super(fm);
        myContext = guruSwamiDetailsActivity;
        this.totalTabs = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                VivaranaFragment vivaranaFragment = new VivaranaFragment();
                return vivaranaFragment;
            case 1:
                SwiyaCharitraFragment swiyaCharitraFragment = new SwiyaCharitraFragment();
                return swiyaCharitraFragment;
            case 2:
                SandasamFragment sandasamFragment = new SandasamFragment();
                return sandasamFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
