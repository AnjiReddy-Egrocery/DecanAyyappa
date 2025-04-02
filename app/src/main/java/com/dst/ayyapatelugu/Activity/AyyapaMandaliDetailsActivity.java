package com.dst.ayyapatelugu.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.R;

import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

public class AyyapaMandaliDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;

    TextView txtname, txtGuruName, txtVillage, txtNumber, txtEmail, txtDiscription;
    ImageView imageView;

    TabLayout tabLayout;
    ViewPager viewPager;

    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayyapa_mandali_details);


        toolbar = findViewById(R.id.toolbar);
       /* toolbar.setLogo(R.drawable.user_profile_background);
        toolbar.setTitle("www.ayyappatelugu.com");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));*/
        setSupportActionBar(toolbar);
        ;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable nav = toolbar.getNavigationIcon();
        if (nav != null) {
            nav.setTint(getResources().getColor(R.color.white));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageAnadanam=findViewById(R.id.layout_image_anadanam);
        imageAnadanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyapaMandaliDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyapaMandaliDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(AyyapaMandaliDetailsActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyapaMandaliDetailsActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });


        txtname = findViewById(R.id.txt_name);
        txtGuruName = findViewById(R.id.txt_guru_name);
        txtVillage = findViewById(R.id.txt_village);
        txtNumber = findViewById(R.id.txt_number);
        txtEmail = findViewById(R.id.txt_email);
        imageView = findViewById(R.id.image_view);
        txtDiscription = findViewById(R.id.txt_discription);
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("ItemName");
        String guruName = bundle.getString("ItemGuruName");
        String village = bundle.getString("ItemCity");
        String number = bundle.getString("ItemNumber");
        String email = bundle.getString("ItemEmail");
        String discription = bundle.getString("Discription");
        txtname.setText(name);
        txtGuruName.setText(guruName);
        txtVillage.setText(village);
        txtNumber.setText(number);
        txtEmail.setText(email);

        Spanned spanned=Html.fromHtml(discription);
        String plainText=spanned.toString();
        txtDiscription.setText(plainText);
        Linkify.addLinks(txtDiscription,Linkify.WEB_URLS);

        String image_path = bundle.getString("imagePath");
        Picasso.get().load(image_path).into(imageView);

    }
}