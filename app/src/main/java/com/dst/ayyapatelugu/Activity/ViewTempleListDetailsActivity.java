package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

public class ViewTempleListDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView txtname, txttname, txtstarttime, txtendTime, txtlocation;
    ImageView imageView;
    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_temple_list_details);

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
                Intent intent=new Intent(ViewTempleListDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewTempleListDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });
        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ViewTempleListDetailsActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewTempleListDetailsActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });



        imageView = findViewById(R.id.img);
        txtname = findViewById(R.id.txt_fname);
        txttname = findViewById(R.id.txt_city);
        txtstarttime = findViewById(R.id.txt_spec);
        txtendTime = findViewById(R.id.fname);
        txtlocation = findViewById(R.id.txt_location);


        Bundle bundle = getIntent().getExtras();

        String dname = bundle.getString("Name");
        String fname = bundle.getString("TName");
        String city = bundle.getString("Open");
        String Specilization = bundle.getString("Close");
        String village = bundle.getString("Location");
        String imagePath = bundle.getString("imagePath");

        txtname.setText(dname);
        txttname.setText(fname);
        txtstarttime.setText(city);
        txtendTime.setText(Specilization);
        txtlocation.setText(village);
        Picasso.get().load(imagePath).into(imageView);


    }
}