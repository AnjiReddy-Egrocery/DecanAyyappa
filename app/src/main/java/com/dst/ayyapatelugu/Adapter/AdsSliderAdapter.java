package com.dst.ayyapatelugu.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dst.ayyapatelugu.Model.AddImageResponse;
import com.dst.ayyapatelugu.Services.LinkRouter;

import java.util.List;

public class AdsSliderAdapter extends RecyclerView.Adapter<AdsSliderAdapter.VH> {

    private final Context context;
    private final List<AddImageResponse.AdItem> items;
    private final String baseUrl;

    public AdsSliderAdapter(Context context, String baseUrl, List<AddImageResponse.AdItem> items) {
        this.context = context;
        this.baseUrl = baseUrl == null ? "" : baseUrl;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView iv = new ImageView(parent.getContext());
        iv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        return new VH(iv);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        AddImageResponse.AdItem item = items.get(position % items.size());
        ImageView iv = (ImageView) holder.itemView;

        Glide.with(context)
                .load(baseUrl + item.image)
                .into(iv);

        iv.setOnClickListener(v -> openLink(item.adimageUrl));
    }

    private void openLink(String url) {
        LinkRouter.open(context, url);   // app:// -> inside app, otherwise browser
    }

    @Override
    public int getItemCount() {
        if (items == null || items.isEmpty()) return 0;
        return items.size() > 1 ? Integer.MAX_VALUE : items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        VH(View v) { super(v); }
    }
}
