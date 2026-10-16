package com.dst.ayyapatelugu.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dst.ayyapatelugu.Model.BannerResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.LinkRouter;

import java.util.List;

public class HeroSliderAdapter extends RecyclerView.Adapter<HeroSliderAdapter.HeroViewHolder> {

    private final Context context;
    private final String imageBaseUrl;
    private final List<BannerResponse.BannerItem> banners;

    public HeroSliderAdapter(Context context, String imageBaseUrl, List<BannerResponse.BannerItem> banners) {
        this.context = context;
        this.imageBaseUrl = imageBaseUrl == null ? "" : imageBaseUrl;
        this.banners = banners;
    }

    // Banner images are 950 x 300 (19:6). Keep the same ratio for every device.
    public static int imageHeightPx(Context c) {
        return Math.round(c.getResources().getDisplayMetrics().widthPixels * 6f / 19f);
    }

    @NonNull
    @Override
    public HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_hero_slide, parent, false);
        return new HeroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HeroViewHolder h, int position) {
        final BannerResponse.BannerItem b = banners.get(position);

        // Image
        Glide.with(context)
                .load(imageBaseUrl + b.image)
                .into(h.image);

        // Title
        h.title.setText(b.title == null ? "" : b.title);

        // Subtitle = description (hidden when empty)
        if (!TextUtils.isEmpty(b.description)) {
            h.subtitle.setText(b.description);
            h.subtitle.setVisibility(View.VISIBLE);
        } else {
            h.subtitle.setVisibility(View.GONE);
        }

        // Transparent box behind title + subtitle only when there is some text
        boolean hasText = !TextUtils.isEmpty(b.title) || !TextUtils.isEmpty(b.description);
        h.textBox.setVisibility(hasText ? View.VISIBLE : View.GONE);

        boolean hasBtn1 = !TextUtils.isEmpty(b.buttonText1) && !TextUtils.isEmpty(b.buttonUrl1);
        boolean hasBtn2 = !TextUtils.isEmpty(b.buttonText2) && !TextUtils.isEmpty(b.buttonUrl2);

        // Whole button row hidden when the banner has no buttons
        h.buttonRow.setVisibility(hasBtn1 || hasBtn2 ? View.VISIBLE : View.GONE);

        if (hasBtn1) {
            h.primary.setText(b.buttonText1 + "  \u203A");
            h.primary.setVisibility(View.VISIBLE);
            h.primary.setOnClickListener(v -> openUrl(b.buttonUrl1));
        } else {
            h.primary.setVisibility(View.GONE);
            h.primary.setOnClickListener(null);
        }

        if (hasBtn2) {
            h.secondary.setText(b.buttonText2);
            h.secondary.setVisibility(View.VISIBLE);
            h.secondary.setOnClickListener(v -> openUrl(b.buttonUrl2));
        } else {
            h.secondary.setVisibility(View.GONE);
            h.secondary.setOnClickListener(null);
        }

        // Tapping elsewhere on the slide opens button 1's link (only if there is one)
        if (hasBtn1) {
            h.itemView.setOnClickListener(v -> openUrl(b.buttonUrl1));
        } else {
            h.itemView.setOnClickListener(null);
            h.itemView.setClickable(false);
        }
    }

    private void openUrl(String url) {
        LinkRouter.open(context, url);   // app:// -> inside app, otherwise browser
    }

    @Override
    public int getItemCount() {
        return banners == null ? 0 : banners.size();
    }

    static class HeroViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView title, subtitle, primary, secondary;
        final View buttonRow, textBox;

        HeroViewHolder(@NonNull View itemView) {
            super(itemView);
            textBox = itemView.findViewById(R.id.heroTextBox);
            image = itemView.findViewById(R.id.heroImage);
            title = itemView.findViewById(R.id.heroTitle);
            subtitle = itemView.findViewById(R.id.heroSubtitle);
            buttonRow = itemView.findViewById(R.id.heroButtonRow);
            primary = itemView.findViewById(R.id.heroPrimaryBtn);
            secondary = itemView.findViewById(R.id.heroSecondaryBtn);
        }
    }
}
