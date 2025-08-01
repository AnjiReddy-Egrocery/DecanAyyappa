package com.dst.ayyapatelugu.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

public class AyyapaPetamDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txtFname, txtDname, txtVillage, txtnumber, txtemail, txtSpecilization, txtDiscription, txtGName;
    ImageView imageView;
    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayyapa_petam_details);

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
                Intent intent=new Intent(AyyapaPetamDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyapaPetamDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });


        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(AyyapaPetamDetailsActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyapaPetamDetailsActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });


        imageView = findViewById(R.id.img);
        txtDname = findViewById(R.id.txt_name);
        txtFname = findViewById(R.id.txt_fname);
        //txtCity = findViewById(R.id.txt_city);
        txtSpecilization = findViewById(R.id.txt_spec);
        txtVillage = findViewById(R.id.txt_village);
        txtnumber = findViewById(R.id.number);
        txtemail = findViewById(R.id.txt_email);
        txtDiscription = findViewById(R.id.txt_description);
        txtGName = findViewById(R.id.fname);

        Bundle bundle = getIntent().getExtras();

        String dname = bundle.getString("Dname");
        String fname = bundle.getString("Fname");
        String city = bundle.getString("City");
        String Specilization = bundle.getString("Specilization");
        String village = bundle.getString("Village");
        String number = bundle.getString("Number");
        String email = bundle.getString("Email");
        String discription = bundle.getString("Discription");
        String gName = bundle.getString("Fname");
        String imagePath = bundle.getString("ImagePath");

        txtDname.setText(dname);
        //txtFname.setText(fname);
        //txtCity.setText(city);
        txtSpecilization.setText(Specilization);
        txtVillage.setText(village);
        txtnumber.setText(number);
        txtemail.setText(email);
        txtDiscription.setText(discription);
        txtGName.setText(gName);
        Picasso.get().load(imagePath).into(imageView);

        txtFname.setText(fname + " , " + city);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtDiscription.setText(Html.fromHtml(discription));

        }
    }
}