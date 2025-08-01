package com.dst.ayyapatelugu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.dst.ayyapatelugu.Activity.AboutActivity;
import com.dst.ayyapatelugu.Activity.AnadanamActivity;
import com.dst.ayyapatelugu.Activity.AyyaappaDevlyaluActivity;
import com.dst.ayyapatelugu.Activity.AyyapaBooksListActivity;
import com.dst.ayyapatelugu.Activity.AyyappaBajanaSognsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaKaryamListActivity;
import com.dst.ayyapatelugu.Activity.AyyappaMandaliListActivity;
import com.dst.ayyapatelugu.Activity.AyyappaPetamListActivity;
import com.dst.ayyapatelugu.Activity.AyyappaTourseDetailsACtivity;
import com.dst.ayyapatelugu.Activity.CalenderActivity;
import com.dst.ayyapatelugu.Activity.DevlyaluActivity;
import com.dst.ayyapatelugu.Activity.GuruSwamiListActivity;
import com.dst.ayyapatelugu.Activity.NityaPoojaActivity;
import com.dst.ayyapatelugu.Activity.ProductsListActivity;
import com.dst.ayyapatelugu.Activity.SevaDetailsActivity;
import com.dst.ayyapatelugu.Activity.SharanughoshaActivity;
import com.dst.ayyapatelugu.Activity.ViewAllAyyappaTemplesActivity;
import com.dst.ayyapatelugu.Activity.ViewAllDetailsActivity;
import com.dst.ayyapatelugu.Activity.ViewAllNewsDetailsActivity;
import com.dst.ayyapatelugu.Activity.ViewAllNewsListActivity;
import com.dst.ayyapatelugu.Activity.ViewAllTemplesActivity;
import com.dst.ayyapatelugu.Adapter.AyyappaListAdapter;
import com.dst.ayyapatelugu.Adapter.AyyappaTemplesListAdapter;
import com.dst.ayyapatelugu.Adapter.NewsListAdapter;
import com.dst.ayyapatelugu.Adapter.SevaListAdapter;
import com.dst.ayyapatelugu.Adapter.ViewAllListAdapter;
import com.dst.ayyapatelugu.Adapter.ViewAllNewsListAdapter;
import com.dst.ayyapatelugu.DataBase.SharedPrefManager;
import com.dst.ayyapatelugu.DataBase.SharedPreferencesHelper;
import com.dst.ayyapatelugu.DataBase.SharedPreferencesManager;
import com.dst.ayyapatelugu.Model.AyyaTempleListModel;
import com.dst.ayyapatelugu.Model.AyyappaTempleList;
import com.dst.ayyapatelugu.Model.LoginDataResponse;
import com.dst.ayyapatelugu.Model.NewsList;
import com.dst.ayyapatelugu.Model.NewsListModel;
import com.dst.ayyapatelugu.Model.SevaList;
import com.dst.ayyapatelugu.Model.SevaListModel;
import com.dst.ayyapatelugu.Model.TemplesList;
import com.dst.ayyapatelugu.Model.TemplesListModel;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.ImageLoader;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.dst.ayyapatelugu.User.LoginActivity;
import com.dst.ayyapatelugu.User.PrivacyPolicyActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView mNavigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    TextView txtName, txtEmail;
    ImageView imageView;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    //LinearLayout layoutSeva;
   // Button buttonViewAll;
    List<SevaListModel> sevaList;
    private Retrofit retrofit;

    Button butBajanaSongs;
    LinearLayout layoutBajanaSongs;

    Button butAllNews;
    // RecyclerView recyclerviewnews;

    List<NewsListModel> newsList;
    LinearLayout layoutNews;
    private boolean isActivityOpened = false;

    NewsListAdapter viewNewsListAdapter;




    List<TemplesListModel> templeList;
    RecyclerView recyclerviewTemples;

    AyyappaTemplesListAdapter ayyappaTemplesListAdapter;
    private LinearLayoutManager layoutManagerTemple;



    List<AyyaTempleListModel> ayyaTempleListModels;
    RecyclerView recyclerviewAyyappaTemples;

    AyyappaListAdapter ayyappaListAdapter;
    private LinearLayoutManager layoutManagerAyyappaTemple;


    private static final int REQUEST_PERMISSION_CODE = 1;

    LinearLayout layoutBhajanamandali,layoutGuruSwami,layoutPoojaPetam,layoutCalender,layoutTourse,layoutAnadanam,layoutBooks,layoutProducts;
    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    private ImageView leftIcon, rightIcon,leftIconAyyappa, rightIconAyyappa,leftnews,rightnews;

    Button butViewAll,viewallButton;

    LinearLayout layoutSharanughosha,layoutNityaPooja;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setLogo(R.drawable.user_profile_background);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer);
        mNavigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toolbar.setLogoDescription(getResources().getString(R.string.title_tool_bar)); // set description for the logo
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        layoutBhajanamandali = findViewById(R.id.layout_bhajana_mandali);
        layoutBhajanamandali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mandaliIntent=new Intent(HomeActivity.this,AyyappaMandaliListActivity.class);
                startActivity(mandaliIntent);
            }
        });
        layoutGuruSwami = findViewById(R.id.layout_guru_swami);
        layoutGuruSwami.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent guruswamiIntent=new Intent(HomeActivity.this,GuruSwamiListActivity.class);
                startActivity(guruswamiIntent);
            }
        });
        layoutPoojaPetam = findViewById(R.id.layout_poojapetam);
        layoutPoojaPetam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,AyyappaPetamListActivity.class);
                startActivity(intent);
            }
        });
        layoutSharanughosha = findViewById(R.id.layout_sharanughosha);
        layoutSharanughosha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(HomeActivity.this, SharanughoshaActivity.class);
                startActivity(intent);
            }
        });
        layoutNityaPooja = findViewById(R.id.layout_nityapooja);
        layoutNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });
        layoutCalender= findViewById(R.id.layout_calender);
        layoutCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calenderIntent=new Intent(HomeActivity.this,CalenderActivity.class);
                startActivity(calenderIntent);
            }
        });
        layoutTourse = findViewById(R.id.layout_tourse);
        layoutTourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tourseIntent=new Intent(HomeActivity.this,AyyappaTourseDetailsACtivity.class);
                startActivity(tourseIntent);
            }
        });
        imageAnadanam=findViewById(R.id.layout_image_anadanam);
        imageAnadanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });
        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        layoutAnadanam = findViewById(R.id.layout_anadanam);
        layoutAnadanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(HomeActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });
        layoutBooks = findViewById(R.id.layout_books);
        layoutBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,AyyapaBooksListActivity.class);
                startActivity(intent);
            }
        });

        layoutProducts = findViewById(R.id.layout_products);
        layoutProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(HomeActivity.this, ProductsListActivity.class);
                startActivity(intent);

            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txtName = (TextView) headerView.findViewById(R.id.txt_header_name);
        txtEmail = (TextView) headerView.findViewById(R.id.txt_header_email);
        imageView = (ImageView) headerView.findViewById(R.id.nav_header_image);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(HomeActivity.this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        String name="";
        String email="";


        if (account != null) {
            name = account.getDisplayName().toString();
            email = account.getEmail().toString();
            String image = String.valueOf(account.getPhotoUrl());
            Picasso.get().load(image).into(imageView);
        }else {

            LoginDataResponse.Result result= SharedPrefManager.getInstance(getApplicationContext()).getUserData();
            name=result.getUserFirstName();
            email=result.getUserEmail();

        }

        txtName.setText(name);
        txtEmail.setText(email);


        checkAndRequestPermissions();

        sevaMethod();

        newsMethod();
        bajanaSongsMethod();

        TemplesListMethod();
        AyyaTemplesListMethod();

    }

    private void bajanaSongsMethod() {
       // butBajanaSongs = findViewById(R.id.viewall_but_bajana_songs);
        //layoutBajanaSongs = findViewById(R.id.layout_bajana_songs);

        /*butBajanaSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this, AyyappaBajanaSognsActivity.class);
                startActivity(intent);
            }
        });*/
       /* layoutBajanaSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this, AyyappaBajanaSognsActivity.class);
                startActivity(intent);
            }
        });*/
    }

    private void newsMethod() {
        butAllNews = findViewById(R.id.viewall_but_news);
        layoutNews = findViewById(R.id.layout_news);
        newsList = new ArrayList<>();
        butAllNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentnews=new Intent(HomeActivity.this,ViewAllNewsListActivity.class);
                startActivity(intentnews);
            }
        });
        layoutNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isActivityOpened) {
                    isActivityOpened = true;
                    // Open the desired activity
                    newsfetchDataFromDataBase();

                    // Optionally reset after some time or when the target activity is closed
                    new Handler().postDelayed(() -> isActivityOpened = false, 1000);
                }
                //Toast.makeText(HomeActivity.this, "News Details will be Clicked", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void newsfetchDataFromDataBase() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true) // Bypasses hostname verification
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface apiClient = retrofit.create(APiInterface.class);
        Call<NewsList> call = apiClient.getNewsList();
        call.enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NewsList newsList1 = response.body();
                    newsList = new ArrayList<>(Arrays.asList(newsList1.getResult()));

                    if (!newsList.isEmpty()) {
                        NewsListModel newsListModel = newsList.get(0);

                        String profilepic = newsListModel.getImage();
                        String imageUrl = "https://www.ayyappatelugu.com/assets/news_images/" + profilepic;
                        String name = newsListModel.getNewsTitle();

                        Intent intent = new Intent(HomeActivity.this, ViewAllNewsDetailsActivity.class);
                        intent.putExtra("Name", name);
                        intent.putExtra("imagePath", imageUrl);
                        intent.putExtra("position", 0); // Passing position
                        startActivity(intent);
                    } else {
                        Log.e("NewsFetch", "No news items available");
                    }
                } else {
                    Log.e("NewsFetch", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<NewsList> call, Throwable t) {
              /*  newsList = SharedPreferencesManager.getNewsList(HomeActivity.this);
                if (newsList != null && !newsList.isEmpty()) {
                    Log.d("Data Check", "NewsList size: " + newsList.size());
                    //newsupdateRecyclerView(newsList);
                }*/

            }
        });
    }

    private void AyyaTemplesListMethod() {
        recyclerviewAyyappaTemples = findViewById(R.id.recycler_ayyappa_temples);
        viewallButton = findViewById(R.id.viewall_but);
        viewallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(HomeActivity.this, ViewAllAyyappaTemplesActivity.class);
                startActivity(intent);
            }
        });
        leftIconAyyappa = findViewById(R.id.leftIcon1);
        rightIconAyyappa = findViewById(R.id.rightIcon1);
        ayyaTempleListModels = new ArrayList<>();

        layoutManagerAyyappaTemple =new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerviewAyyappaTemples.setLayoutManager(layoutManagerAyyappaTemple);

        ayyappafechedDatafromShredPreferences();

        leftIconAyyappa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollRecyclerViewLeftayyappa();
            }
        });

        rightIconAyyappa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollRecyclerViewRightayyappa();
            }
        });

    }

    private void scrollRecyclerViewRightayyappa() {
        int visiblePosition = layoutManagerAyyappaTemple.findLastVisibleItemPosition();
        if (visiblePosition < ayyappaListAdapter.getItemCount() - 1) {
            recyclerviewAyyappaTemples.smoothScrollToPosition(visiblePosition + 1);
        }
    }

    private void scrollRecyclerViewLeftayyappa() {
        int visiblePosition = layoutManagerAyyappaTemple.findFirstVisibleItemPosition();
        if (visiblePosition > 0) {
            recyclerviewAyyappaTemples.smoothScrollToPosition(visiblePosition - 1);
        }
    }

    private void ayyappafechedDatafromShredPreferences() {
        List<AyyaTempleListModel> templesListModels= SharedPreferencesManager.getAyyappaTemplesList(HomeActivity.this);
        if (templesListModels != null && !templesListModels.isEmpty()) {
            // Data exists in SharedPreferences, update RecyclerView
            ayyappaupdateRecyclerView(templesListModels);
        } else {
            // Data doesn't exist in SharedPreferences, fetch from the network
           ayyappafetchDataFromDataBase();
        }
    }

    private void ayyappafetchDataFromDataBase() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true) // Bypasses hostname verification
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface apiClient = retrofit.create(APiInterface.class);
        Call<AyyappaTempleList> modelCall=apiClient.getAyyappaTempleList();
        modelCall.enqueue(new Callback<AyyappaTempleList>() {
            @Override
            public void onResponse(Call<AyyappaTempleList> call, Response<AyyappaTempleList> response) {
                AyyappaTempleList list=response.body();
                ayyaTempleListModels=new ArrayList<>(Arrays.asList(list.getResult()));

                SharedPreferencesManager.saveAyyappaTempleList(HomeActivity.this, ayyaTempleListModels);

                ayyappaupdateRecyclerView(ayyaTempleListModels);

            }

            @Override
            public void onFailure(Call<AyyappaTempleList> call, Throwable t) {
                ayyaTempleListModels = SharedPreferencesManager.getAyyappaTemplesList(HomeActivity.this);
                if (ayyaTempleListModels != null && !ayyaTempleListModels.isEmpty()) {
                    // Update the RecyclerView
                    ayyappaupdateRecyclerView(ayyaTempleListModels);
                }

            }
        });

    }

    private void ayyappaupdateRecyclerView(List<AyyaTempleListModel> templesListModels) {
        List<AyyaTempleListModel> limitedList = templesListModels.size() > 10
                ? templesListModels.subList(0, 10)
                : templesListModels;

        ayyappaListAdapter = new AyyappaListAdapter(HomeActivity.this,limitedList);
        recyclerviewAyyappaTemples.setAdapter(ayyappaListAdapter);
    }

    private void TemplesListMethod() {
        recyclerviewTemples = findViewById(R.id.recycler_temples);

        butViewAll = findViewById(R.id.but_viewAll);
        butViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(HomeActivity.this, ViewAllTemplesActivity.class);
                startActivity(intent);
            }
        });
        leftIcon = findViewById(R.id.leftIcon);
        rightIcon = findViewById(R.id.rightIcon);
        templeList = new ArrayList<>();

         layoutManagerTemple =new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerviewTemples.setLayoutManager(layoutManagerTemple);

        fechedDatafromShredPreferences();

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollRecyclerViewLeft();
            }
        });

        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollRecyclerViewRight();
            }
        });
    }

    private void scrollRecyclerViewRight() {
        int visiblePosition = layoutManagerTemple.findLastVisibleItemPosition();
        if (visiblePosition < ayyappaTemplesListAdapter.getItemCount() - 1) {
            recyclerviewTemples.smoothScrollToPosition(visiblePosition + 1);
        }
    }

    private void scrollRecyclerViewLeft() {
        int visiblePosition = layoutManagerTemple.findFirstVisibleItemPosition();
        if (visiblePosition > 0) {
            recyclerviewTemples.smoothScrollToPosition(visiblePosition - 1);
        }
    }

    private void fechedDatafromShredPreferences() {
        List<TemplesListModel> templesListModels= SharedPreferencesManager.getTemplesList(HomeActivity.this);
        if (templesListModels != null && !templesListModels.isEmpty()) {
            // Data exists in SharedPreferences, update RecyclerView
            updateRecyclerView(templesListModels);
        } else {
            // Data doesn't exist in SharedPreferences, fetch from the network
            fetchDataFromDataBase();
        }
    }

    private void fetchDataFromDataBase() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true) // Bypasses hostname verification
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface apiClient = retrofit.create(APiInterface.class);
        Call<TemplesList> modelCall=apiClient.getTempleList();
        modelCall.enqueue(new Callback<TemplesList>() {
            @Override
            public void onResponse(Call<TemplesList> call, Response<TemplesList> response) {
                TemplesList list=response.body();
                templeList=new ArrayList<>(Arrays.asList(list.getResult()));

                SharedPreferencesManager.saveTempleList(HomeActivity.this, templeList);

                updateRecyclerView(templeList);

            }

            @Override
            public void onFailure(Call<TemplesList> call, Throwable t) {
                templeList = SharedPreferencesManager.getTemplesList(HomeActivity.this);
                if (templeList != null && !templeList.isEmpty()) {
                    // Update the RecyclerView
                    updateRecyclerView(templeList);
                }

            }
        });

    }

    private void updateRecyclerView(List<TemplesListModel> templesListModels) {

        List<TemplesListModel> limitedList = templesListModels.size() > 10
                ? templesListModels.subList(0, 10)
                : templesListModels;

        // Set up the adapter with the limited list
        ayyappaTemplesListAdapter = new AyyappaTemplesListAdapter(HomeActivity.this, limitedList);
        recyclerviewTemples.setAdapter(ayyappaTemplesListAdapter);
    }



    private void sevaMethod() {
//        buttonViewAll = findViewById(R.id.but_view_all);
       // layoutSeva = findViewById(R.id.layout_seva);
        /*buttonViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this, ViewAllDetailsActivity.class);
                startActivity(intent);

            }
        });*/

      /*  layoutSeva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isActivityOpened) {
                    isActivityOpened = true;
                    // Open the desired activity
                    SevaDtailsMethod();

                    // Optionally reset after some time or when the target activity is closed
                    new Handler().postDelayed(() -> isActivityOpened = false, 1000);
                }

            }
        });*/

    }

    private void SevaDtailsMethod() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Change to Level.BASIC for less detail

        // Create OkHttpClient without SSL bypassing
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Add the logging interceptor
                .build();

        // Initialize Retrofit with the OkHttpClient
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/") // Ensure this is your correct base URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface apiClient = retrofit.create(APiInterface.class);

        Call<SevaList> call = apiClient.getSevaList();
        call.enqueue(new Callback<SevaList>() {
            @Override
            public void onResponse(Call<SevaList> call, Response<SevaList> response) {
                if (response.isSuccessful()) {
                    SevaList data = response.body();
                    sevaList=new ArrayList<>(Arrays.asList(data.getResult()));
                    SevaListModel modal = sevaList.get(0);
                    String profilePic = modal.getImage();
                    String imgUrl = "https://www.ayyappatelugu.com/assets/seva_samasthalu/" + profilePic;
                    String name = modal.getTitle();
                    String smalldiscription= modal.getSmalldescription();
                    String discription = modal.getDescription();;

                    Intent intent = new Intent(HomeActivity.this, SevaDetailsActivity.class);
                    intent.putExtra("ItemName", name);
                    intent.putExtra("SmallDiscription", smalldiscription);
                    intent.putExtra("imagePath", imgUrl);
                    intent.putExtra("Discription", discription);
                    startActivity(intent);

                } else {
                    // Handle unsuccessful response
                    // You might want to show an error message or handle it in some way
                    Log.e("API Response", "Unsuccessful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SevaList> call, Throwable t) {
                // Handle failure, for example, show an error message
                Log.e("API Failure", "Error: " + t.getMessage());
            }
        });
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_NUMBERS

        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!arePermissionsGranted(permissions)) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
            }
        }

    }

    private boolean arePermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (!areAllPermissionsGranted(grantResults)) {
                // showPermissionDeniedDialog();
            }
        }
    }




    @Override
    public void onBackPressed() {
        showExitConfirmationDialog(); // Show the exit confirmation dialog first
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit Ayyappa Telugu?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Dismiss the dialog
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private boolean areAllPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int action = item.getItemId();
        if (action == R.id.Ayyapa_karyam) {

            Intent intent = new Intent(HomeActivity.this, AyyappaKaryamListActivity.class);
            startActivity(intent);

        } else if (action == R.id.guruswami_list) {

            Intent intent = new Intent(HomeActivity.this, GuruSwamiListActivity.class);
            startActivity(intent);

        } else if (action == R.id.ayyappa_mandali) {

            Intent intent = new Intent(HomeActivity.this, AyyappaMandaliListActivity.class);
            startActivity(intent);

        }else if (action == R.id.ayyappa_decration) {

            Intent intent = new Intent(HomeActivity.this, AyyappaPetamListActivity.class);
            startActivity(intent);

        } else if (action == R.id.ayyappa_tours) {

            Intent intent = new Intent(HomeActivity.this, AyyappaTourseDetailsACtivity.class);
            startActivity(intent);

        } else if (action == R.id.ayyappa_books) {

            Intent intent = new Intent(HomeActivity.this, AyyapaBooksListActivity.class);
            startActivity(intent);
        } else if (action == R.id.ayyappa_details) {

            Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (action == R.id.ayyappa_calender){

            Intent intent=new Intent(HomeActivity.this, CalenderActivity.class);
            startActivity(intent);


        } else if(action == R.id.ayyappa_anadanam){

            Intent intent=new Intent(HomeActivity.this, AnadanamActivity.class);
            startActivity(intent);

        }else if(action == R.id.ayyappa_devalyalu){

            Intent intent=new Intent(HomeActivity.this, ViewAllTemplesActivity.class);
            startActivity(intent);

        }else if(action == R.id.ayyappa_ayyappadevlyalu){

            Intent intent=new Intent(HomeActivity.this, ViewAllAyyappaTemplesActivity.class);
            startActivity(intent);

        }else if(action == R.id.ayyappa_policy){

            Intent intent=new Intent(HomeActivity.this, PrivacyPolicyActivity.class);
            startActivity(intent);

        }else if (action == R.id.log_out) {
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    SharedPrefManager.getInstance(getApplicationContext()).isLoggedOut();
                    finish();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if the user is logged in
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }



}
