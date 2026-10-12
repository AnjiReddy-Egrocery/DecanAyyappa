package com.dst.ayyapatelugu.Model;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

/** Static data for one slide of the home-screen hero carousel. */
public class HeroSlide {
    public final String badge;
    public final String title;
    @Nullable public final String subtitle;
    @DrawableRes public final int imageRes;
    public final String primaryText;
    public final Class<?> primaryTarget;
    @Nullable public final String secondaryText;
    @Nullable public final Class<?> secondaryTarget;

    public HeroSlide(String badge, String title, @Nullable String subtitle,
                     @DrawableRes int imageRes,
                     String primaryText, Class<?> primaryTarget,
                     @Nullable String secondaryText, @Nullable Class<?> secondaryTarget) {
        this.badge = badge;
        this.title = title;
        this.subtitle = subtitle;
        this.imageRes = imageRes;
        this.primaryText = primaryText;
        this.primaryTarget = primaryTarget;
        this.secondaryText = secondaryText;
        this.secondaryTarget = secondaryTarget;
    }
}
