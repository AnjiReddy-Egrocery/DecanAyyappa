package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dst.ayyapatelugu.R;

public class BottomCardBitmapFactory {
    public static Bitmap create(
            Context context,
            String name,
            String designation) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.bottom_card_layout, null, false);

        TextView txtName =
                view.findViewById(R.id.txtName);

        TextView txtDesignation =
                view.findViewById(R.id.txtDesignation);

        txtName.setText(name);
        txtDesignation.setText(designation);

        view.measure(
                View.MeasureSpec.makeMeasureSpec(
                        1080,
                        View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(
                        170,
                        View.MeasureSpec.EXACTLY)
        );

        view.layout(0, 0, 1080, 170);

        Bitmap bitmap = Bitmap.createBitmap(
                1080,
                170,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        view.draw(canvas);

        return bitmap;
    }
}
