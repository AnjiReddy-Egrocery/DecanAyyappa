package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.Adapter.NearbyAyyappaTempleAdapter;
import com.dst.ayyapatelugu.Adapter.NearbyTempleAdapter;
import com.dst.ayyapatelugu.DataBase.SharedPreferenceHelper;
import com.dst.ayyapatelugu.Model.AyyappaTempleMapDataResponse;
import com.dst.ayyapatelugu.Model.TempleMapDataResponse;
import com.dst.ayyapatelugu.R;

import java.util.ArrayList;

public class NearbyAyyappaTempleListActivity extends AppCompatActivity implements NearbyAyyappaTempleAdapter.OnTempleClickListener {

    RecyclerView recyclerView;

    ArrayList<AyyappaTempleMapDataResponse.Result> list;

    NearbyAyyappaTempleAdapter adapter;
    Toolbar toolbar;
    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_ayyappa_temple_list);

        recyclerView = findViewById(R.id.recyclerView);

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
                Intent intent=new Intent(NearbyAyyappaTempleListActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NearbyAyyappaTempleListActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });
        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(NearbyAyyappaTempleListActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NearbyAyyappaTempleListActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });


        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));

        list = SharedPreferenceHelper
                .getNearbyAyyappaTemple(this);

        adapter =
                new NearbyAyyappaTempleAdapter(
                        this,
                        list,
                        this);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onTempleClick(AyyappaTempleMapDataResponse.Result item) {

        Intent i =
                new Intent(
                        this,
                        ViewAllAyyappaTemplesActivity.class);

        i.putExtra(
                "OPEN_AYYAPPATEMPLE",
                true);

        i.putExtra(
                "TEMPLE_ID",
                item.getTempleId());

        i.putExtra(
                "TEMPLE_LAT",
                item.getLatitude());

        i.putExtra(
                "TEMPLE_LNG",
                item.getLongitude());

        startActivity(i);
    }
}