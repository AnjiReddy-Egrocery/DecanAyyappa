package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Model.HeroSlide;
import com.dst.ayyapatelugu.R;

import java.util.List;

public class HeroSliderAdapter extends RecyclerView.Adapter<HeroSliderAdapter.HeroViewHolder> {

    private final Context context;
    private final List<HeroSlide> slides;

    public HeroSliderAdapter(Context context, List<HeroSlide> slides) {
        this.context = context;
        this.slides = slides;
    }

    @NonNull
    @Override
    public HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_hero_slide, parent, false);
        return new HeroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HeroViewHolder h, int position) {
        final HeroSlide s = slides.get(position);

        h.image.setImageResource(s.imageRes);
        h.title.setText(s.title);

        if (s.subtitle != null && !s.subtitle.isEmpty()) {
            h.subtitle.setText(s.subtitle);
            h.subtitle.setVisibility(View.VISIBLE);
        } else {
            h.subtitle.setVisibility(View.GONE);
        }

        h.primary.setText(s.primaryText + "  \u203A");
        h.primary.setOnClickListener(v -> open(s.primaryTarget));

        if (s.secondaryText != null && s.secondaryTarget != null) {
            h.secondary.setText(s.secondaryText);
            h.secondary.setVisibility(View.VISIBLE);
            h.secondary.setOnClickListener(v -> open(s.secondaryTarget));
        } else {
            h.secondary.setVisibility(View.GONE);
        }

        // Tapping anywhere else on the slide opens the primary target
        h.itemView.setOnClickListener(v -> open(s.primaryTarget));
    }

    private void open(Class<?> target) {
        context.startActivity(new Intent(context, target));
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    static class HeroViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView  title, subtitle, primary, secondary;

        HeroViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.heroImage);

            title = itemView.findViewById(R.id.heroTitle);
            subtitle = itemView.findViewById(R.id.heroSubtitle);
            primary = itemView.findViewById(R.id.heroPrimaryBtn);
            secondary = itemView.findViewById(R.id.heroSecondaryBtn);
        }
    }
}
