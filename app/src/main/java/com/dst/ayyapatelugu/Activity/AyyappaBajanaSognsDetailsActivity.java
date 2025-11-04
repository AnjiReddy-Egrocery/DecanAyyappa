package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

public class AyyappaBajanaSognsDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;

    TextView txtName,txtSingerNamer;
    WebView webView;



    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayyappa_bajana_sogns_details);

        toolbar = findViewById(R.id.toolbar);

       /* toolbar.setLogo(R.drawable.user_profile_background);


        toolbar.setTitle("www.ayyappatelugu.com");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));*/

        setSupportActionBar(toolbar);
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
                Intent intent=new Intent(AyyappaBajanaSognsDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyappaBajanaSognsDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(AyyappaBajanaSognsDetailsActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyappaBajanaSognsDetailsActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });


        txtName = findViewById(R.id.txt_songname);
        txtSingerNamer = findViewById(R.id.txt_singername);
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true); // if needed
        webView.setBackgroundColor(Color.TRANSPARENT);

        Bundle bundle = getIntent().getExtras();

        String name = bundle.getString("Name");
        String singername = bundle.getString("SingerName");
        String discription = bundle.getString("Discription");


        txtName.setText(name);
        txtSingerNamer.setText(singername);

        String htmlContent = "<html><head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>" +
                "<style>" +
                "body { background-color: transparent; color: white; font-size: 16px; line-height: 1.6; }" +
                "* { color: white !important; }" +
                "</style>" +
                "</head><body>" + discription + "</body></html>";

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            webView.loadDataWithBaseURL(null, discription, "text/html", "utf-8", null);
            ;

            //webView.loadData(discription, "text/html", "UTF-8");

        }*/

    }
}