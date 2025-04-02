package com.dst.ayyapatelugu.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.dst.ayyapatelugu.R;

import java.util.Objects;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    int[] images;

    LayoutInflater mLayoutInflater;

    public ViewPagerAdapter(Context context, int[] images) {
        this.context = context;
        this.images = images;

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }


    @Override
    @SuppressLint("MissingInflatedId")
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        View itemView = mLayoutInflater.inflate(R.layout.image_slider_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewMain);


        imageView.setImageResource(images[position]);

        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }


}
