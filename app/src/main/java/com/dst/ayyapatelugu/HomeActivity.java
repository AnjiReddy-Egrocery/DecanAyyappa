package com.dst.ayyapatelugu;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dst.ayyapatelugu.Activity.AboutActivity;
import com.dst.ayyapatelugu.Activity.AnadanamActivity;
import com.dst.ayyapatelugu.Activity.AyyapaBooksListActivity;
import com.dst.ayyapatelugu.Activity.AyyappaBajanaSognsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaKaryamListActivity;
import com.dst.ayyapatelugu.Activity.AyyappaMandaliListActivity;
import com.dst.ayyapatelugu.Activity.AyyappaPetamListActivity;
import com.dst.ayyapatelugu.Activity.AyyappaTourseDetailsACtivity;
import com.dst.ayyapatelugu.Activity.BlogsActivity;
import com.dst.ayyapatelugu.Activity.CalenderActivity;
import com.dst.ayyapatelugu.Activity.GuruSwamiListActivity;
import com.dst.ayyapatelugu.Activity.ImagesListActivity;
import com.dst.ayyapatelugu.Activity.NityaPoojaActivity;
import com.dst.ayyapatelugu.Activity.PadayatraActivity;
import com.dst.ayyapatelugu.Activity.PanchagamActivity;
import com.dst.ayyapatelugu.Activity.PostVideosActivity;
import com.dst.ayyapatelugu.Activity.ProductsListActivity;
import com.dst.ayyapatelugu.Activity.ProfileActivity;
import com.dst.ayyapatelugu.Activity.SevaDetailsActivity;
import com.dst.ayyapatelugu.Activity.SharanughoshaActivity;
import com.dst.ayyapatelugu.Activity.TeluguCalenderActivity;
import com.dst.ayyapatelugu.Activity.ViewAllAyyappaTemplesActivity;
import com.dst.ayyapatelugu.Activity.ViewAllNewsListActivity;
import com.dst.ayyapatelugu.Activity.ViewAllTemplesActivity;
import com.dst.ayyapatelugu.Adapter.AdsSliderAdapter;
import com.dst.ayyapatelugu.Adapter.AllNewsListAdapter;
import com.dst.ayyapatelugu.Adapter.HeroSliderAdapter;
import com.dst.ayyapatelugu.Adapter.AyyappaListAdapter;
import com.dst.ayyapatelugu.Adapter.AyyappaTemplesListAdapter;
import com.dst.ayyapatelugu.DataBase.SharedPrefManager;
import com.dst.ayyapatelugu.DataBase.SharedPreferencesManager;
import com.dst.ayyapatelugu.Model.AdClickResponse;
import com.dst.ayyapatelugu.Model.AddImageResponse;
import com.dst.ayyapatelugu.Model.AyyaTempleListModel;
import com.dst.ayyapatelugu.Model.AyyappaTempleList;
import com.dst.ayyapatelugu.Model.BannerResponse;
import com.dst.ayyapatelugu.Model.LoginDataResponse;
import com.dst.ayyapatelugu.Model.NewsList;
import com.dst.ayyapatelugu.Model.NewsListModel;
import com.dst.ayyapatelugu.Model.SevaList;
import com.dst.ayyapatelugu.Model.SevaListModel;
import com.dst.ayyapatelugu.Model.TemplesList;
import com.dst.ayyapatelugu.Model.TemplesListModel;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.LinkRouter;
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
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
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

    List<SevaListModel> sevaList;
    private Retrofit retrofit;

    Button butAllNews;

    List<TemplesListModel> templeList;
    RecyclerView recyclerviewTemples;
    AyyappaTemplesListAdapter ayyappaTemplesListAdapter;
    private LinearLayoutManager layoutManagerTemple;

    List<AyyaTempleListModel> ayyaTempleListModels;
    RecyclerView recyclerviewAyyappaTemples;
    AyyappaListAdapter ayyappaListAdapter;
    private LinearLayoutManager layoutManagerAyyappaTemple;

    private static final int REQUEST_PERMISSION_CODE = 1;

    LinearLayout layoutBhajanamandali, layoutGuruSwami, layoutPoojaPetam, layoutCalender, layoutTourse,
            layoutAnadanam, layoutBooks, layoutProducts, layoutsharanughosha, layoutNityapooja;
    ImageView imageAnadanam, imageNityaPooja;
    TextView textAndanam, txtNityaPooja;

    private ImageView leftIcon, rightIcon, leftIconAyyappa, rightIconAyyappa;

    Button butViewAll, viewallButton;

    // Hero section (dynamic data from API)
    ViewPager2 heroViewPager;
    LinearLayout heroDots;
    LinearLayout layoutHeroContainer;
    ImageView[] heroDotViews;
    List<BannerResponse.BannerItem> heroBanners = new ArrayList<>();
    Handler heroHandler = new Handler(Looper.getMainLooper());
    Runnable heroRunnable;
    private static final long HERO_AUTO_SLIDE_MS = 4000;
    SwipeRefreshLayout swipeRefreshLayout, swipeRefreshLayout1, swipeRefreshLayout2, swipeRefreshHome;

    RecyclerView recyclerviewnews;
    List<NewsListModel> newsList;
    AllNewsListAdapter viewAllNewsListAdapter;

    LoginDataResponse.Result result;

    ViewPager2 adsViewPager;
    LinearLayout adsDots;
    Handler adsHandler = new Handler(Looper.getMainLooper());
    Runnable adsRunnable;
    List<AddImageResponse.AdItem> adImages = new ArrayList<>();
    ImageView[] adsDotViews;
    LinearLayout layoutAdsContainer;
    int pendingRefresh = 0;
    private boolean adClickInProgress = false;

    // ---------------- In-app update ----------------
    private static final String PLAY_STORE_URL =
            "https://play.google.com/store/apps/details?id=com.dst.ayyapatelugu";
    private static final String PLAY_STORE_MARKET_URL =
            "market://details?id=com.dst.ayyapatelugu";
    private static final String UPDATE_CHANNEL_ID = "app_update";
    private static final int UPDATE_NOTIFICATION_ID = 1001;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 2002;
    private boolean updateDialogShown = false;
    private AlertDialog updateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        swipeRefreshHome = findViewById(R.id.swipeRefreshHome);
        swipeRefreshHome.setColorSchemeColors(0xFFBC0F0F);
        swipeRefreshHome.setOnRefreshListener(this::refreshAll);

        // Back button: predictive back (API 36) ki callback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmationDialog();
            }
        });

        drawerLayout = findViewById(R.id.drawer);
        mNavigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toolbar.setLogoDescription(getResources().getString(R.string.title_tool_bar));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        layoutBhajanamandali = findViewById(R.id.layout_bhajana_mandali);
        layoutBhajanamandali.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AyyappaMandaliListActivity.class)));

        layoutGuruSwami = findViewById(R.id.layout_guru_swami);
        layoutGuruSwami.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, GuruSwamiListActivity.class)));

        layoutPoojaPetam = findViewById(R.id.layout_poojapetam);
        layoutPoojaPetam.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AyyappaPetamListActivity.class)));

        layoutsharanughosha = findViewById(R.id.layout_sharanughosha);
        layoutsharanughosha.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, SharanughoshaActivity.class)));

        layoutNityapooja = findViewById(R.id.layout_nityapooja);
        layoutNityapooja.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, NityaPoojaActivity.class)));

        layoutCalender = findViewById(R.id.layout_calender);
        layoutCalender.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, CalenderActivity.class)));

        layoutTourse = findViewById(R.id.layout_tourse);
        layoutTourse.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AyyappaTourseDetailsACtivity.class)));

        imageAnadanam = findViewById(R.id.layout_image_anadanam);
        imageAnadanam.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AnadanamActivity.class)));

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AnadanamActivity.class)));

        layoutAnadanam = findViewById(R.id.layout_anadanam);
        layoutAnadanam.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AnadanamActivity.class)));

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, NityaPoojaActivity.class)));

        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, NityaPoojaActivity.class)));

        layoutBooks = findViewById(R.id.layout_books);
        layoutBooks.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AyyapaBooksListActivity.class)));

        layoutProducts = findViewById(R.id.layout_products);
        layoutProducts.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ProductsListActivity.class)));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txtName = (TextView) headerView.findViewById(R.id.txt_header_name);
        txtEmail = (TextView) headerView.findViewById(R.id.txt_header_email);
        imageView = (ImageView) headerView.findViewById(R.id.nav_header_image);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(HomeActivity.this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        String name = "";
        String email = "";

        if (account != null) {
            name = account.getDisplayName().toString();
            email = account.getEmail().toString();
            String image = String.valueOf(account.getPhotoUrl());
            Picasso.get().load(image).into(imageView);
        } else {
            result = SharedPrefManager.getInstance(getApplicationContext()).getUserData();

            String firstName = result.getUserFirstName();
            String lastname = result.getUserLastName();

            name = firstName + " " + lastname;
            email = result.getUserEmail();

            txtName.setText(name);
            txtEmail.setText(email);
        }

        txtName.setText(name);
        txtEmail.setText(email);

        String profilePic = SharedPrefManager.getInstance(this).getProfilePic();

        Log.d("HEADER_PROFILE", String.valueOf(profilePic));

        if (profilePic != null
                && !profilePic.isEmpty()
                && !profilePic.startsWith("http")) {
            profilePic = "https://www.ayyappatelugu.com/public/assets/user_images/" + profilePic;
        }

        Log.d("HEADER_PROFILE", "profilePic = " + profilePic);

        Glide.with(this)
                .load(profilePic)
                .circleCrop()
                .into(imageView);

        checkAndRequestPermissions();

        sevaMethod();
        newsMethod();
        bajanaSongsMethod();
        TemplesListMethod();
        AyyaTemplesListMethod();
        adsCarouselMethod();
        heroSectionMethod();

        checkForUpdate();
    }

    // ---------------------------------------------------------------------------------
    // In-app update: dialog (Update / Ignore) + notification
    // ---------------------------------------------------------------------------------
    private void checkForUpdate() {
        AppUpdateManager manager = AppUpdateManagerFactory.create(this);
        manager.getAppUpdateInfo().addOnSuccessListener((AppUpdateInfo info) -> {
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && !updateDialogShown
                    && !isFinishing() && !isDestroyed()) {
                updateDialogShown = true;
                showUpdateDialog();
                showUpdateNotification();
            }
        });
    }

    private void showUpdateDialog() {
        updateDialog = new AlertDialog.Builder(this)
                .setTitle("Update Available")
                .setMessage("A new version of Ayyappa Telugu is available. Please update to get the latest features.")
                .setCancelable(true)
                .setPositiveButton("Update", (dialog, which) -> openPlayStore())
                .setNegativeButton("Ignore", (dialog, which) -> dialog.dismiss())
                .create();
        updateDialog.show();
    }

    private void openPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_MARKET_URL)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL)));
        }
    }

    private void showUpdateNotification() {
        if (Build.VERSION.SDK_INT >= 33
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            return;
        }

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(new NotificationChannel(
                    UPDATE_CHANNEL_ID, "App Updates", NotificationManager.IMPORTANCE_DEFAULT));
        }

        Intent storeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL));
        PendingIntent pi = PendingIntent.getActivity(this, 0, storeIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, UPDATE_CHANNEL_ID)
                .setSmallIcon(R.drawable.applogo)
                .setContentTitle("Update Available")
                .setContentText("A new version of Ayyappa Telugu is available. Tap to update.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi)
                .setAutoCancel(true);

        nm.notify(UPDATE_NOTIFICATION_ID, builder.build());
    }

    // ---------------------------------------------------------------------------------
    // Refresh
    // ---------------------------------------------------------------------------------
    private void refreshAll() {
        pendingRefresh = 5;               // 5 API calls
        loadHeroBanners();
        loadAds();
        newsfetchDataFromDataBase();
        ayyappafetchDataFromDataBase();
        fetchDataFromDataBase();
    }

    private void finishRefresh() {
        if (pendingRefresh > 0) pendingRefresh--;
        if (pendingRefresh <= 0 && swipeRefreshHome != null) {
            swipeRefreshHome.setRefreshing(false);
        }
    }

    // ---------------------------------------------------------------------------------
    // Hero section: full-width carousel, data from GET APICalls/banner-images
    // ---------------------------------------------------------------------------------
    private void heroSectionMethod() {
        heroViewPager = findViewById(R.id.heroViewPager);
        heroDots = findViewById(R.id.heroDots);
        layoutHeroContainer = findViewById(R.id.layout_hero);

        layoutHeroContainer.setVisibility(View.GONE);

        heroViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateHeroDots(position);
                startHeroAutoSlide();
            }
        });

        heroRunnable = () -> {
            if (heroBanners != null && heroBanners.size() > 1) {
                int next = (heroViewPager.getCurrentItem() + 1) % heroBanners.size();
                heroViewPager.setCurrentItem(next, true);
            }
        };

        loadHeroBanners();
    }

    private void loadHeroBanners() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface apiClient = retrofit.create(APiInterface.class);

        apiClient.getBannerImages().enqueue(new Callback<BannerResponse>() {
            @Override
            public void onResponse(Call<BannerResponse> call, Response<BannerResponse> response) {
                finishRefresh();
                if (isFinishing() || isDestroyed()) return;

                BannerResponse body = response.body();
                if (response.isSuccessful() && body != null
                        && "200".equals(body.errorCode)
                        && body.result != null && !body.result.isEmpty()) {
                    showHero(body);
                } else {
                    hideHero();
                }
            }

            @Override
            public void onFailure(Call<BannerResponse> call, Throwable t) {
                finishRefresh();
                Log.e("HERO_API", String.valueOf(t.getMessage()));
                if (!isFinishing() && !isDestroyed()) {
                    hideHero();
                }
            }
        });
    }

    private void showHero(BannerResponse body) {
        heroBanners = body.result;

        ViewGroup.LayoutParams lp = heroViewPager.getLayoutParams();
        lp.height = HeroSliderAdapter.imageHeightPx(this);
        heroViewPager.setLayoutParams(lp);

        heroViewPager.setAdapter(new HeroSliderAdapter(HomeActivity.this, body.imageUrl, heroBanners));

        heroDots.removeAllViews();
        heroDotViews = new ImageView[heroBanners.size()];
        int size = (int) (8 * getResources().getDisplayMetrics().density);
        int margin = (int) (4 * getResources().getDisplayMetrics().density);
        for (int i = 0; i < heroDotViews.length; i++) {
            heroDotViews[i] = new ImageView(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(size, size);
            p.setMargins(margin, 0, margin, 0);
            heroDots.addView(heroDotViews[i], p);
        }
        heroDots.setVisibility(heroBanners.size() > 1 ? View.VISIBLE : View.GONE);
        updateHeroDots(0);

        layoutHeroContainer.setVisibility(View.VISIBLE);
        startHeroAutoSlide();
    }

    private void hideHero() {
        heroBanners = new ArrayList<>();
        heroHandler.removeCallbacks(heroRunnable);
        if (heroViewPager != null) heroViewPager.setAdapter(null);
        if (heroDots != null) heroDots.removeAllViews();
        layoutHeroContainer.setVisibility(View.GONE);
    }

    private void startHeroAutoSlide() {
        if (heroRunnable == null || heroBanners == null || heroBanners.size() < 2) return;
        heroHandler.removeCallbacks(heroRunnable);
        heroHandler.postDelayed(heroRunnable, HERO_AUTO_SLIDE_MS);
    }

    private void updateHeroDots(int position) {
        if (heroDotViews == null) return;
        for (int i = 0; i < heroDotViews.length; i++) {
            heroDotViews[i].setImageResource(i == position ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    // ---------------------------------------------------------------------------------
    // Ads carousel
    // ---------------------------------------------------------------------------------
    private void adsCarouselMethod() {
        adsViewPager = findViewById(R.id.adsViewPager);
        adsDots = findViewById(R.id.adsDots);
        layoutAdsContainer = findViewById(R.id.layout_ads);

        layoutAdsContainer.setVisibility(View.GONE);

        adsViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (adImages == null || adImages.isEmpty()) return;
                updateAdsDots(position % adImages.size());
            }
        });

        adsRunnable = () -> {
            if (adImages != null && adImages.size() > 1) {
                adsViewPager.setCurrentItem(adsViewPager.getCurrentItem() + 1, true);
                adsHandler.postDelayed(adsRunnable, 3000);
            }
        };

        setupAdsTapDetector();
        loadAds();
    }

    private void loadAds() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface apiClient = retrofit.create(APiInterface.class);

        apiClient.getAdImages().enqueue(new Callback<AddImageResponse>() {
            @Override
            public void onResponse(Call<AddImageResponse> call, Response<AddImageResponse> response) {
                finishRefresh();
                if (isFinishing() || isDestroyed()) return;

                AddImageResponse body = response.body();
                Log.d("ADS_API", "http=" + response.code()
                        + " body=" + (body == null ? "null" : new Gson().toJson(body)));

                if (response.isSuccessful() && body != null
                        && "200".equals(body.errorCode)
                        && body.result != null && !body.result.isEmpty()) {
                    showAds(body);
                } else {
                    hideAds();
                }
            }

            @Override
            public void onFailure(Call<AddImageResponse> call, Throwable t) {
                finishRefresh();
                Log.e("ADS_API", String.valueOf(t.getMessage()));
                if (!isFinishing() && !isDestroyed()) {
                    hideAds();
                }
            }
        });
    }

    private void showAds(AddImageResponse body) {
        adImages = body.result;
        adsViewPager.setAdapter(new AdsSliderAdapter(HomeActivity.this, body.imageUrl, adImages));

        adsDots.removeAllViews();
        adsDotViews = new ImageView[adImages.size()];
        int size = (int) (8 * getResources().getDisplayMetrics().density);
        int margin = (int) (4 * getResources().getDisplayMetrics().density);
        for (int i = 0; i < adsDotViews.length; i++) {
            adsDotViews[i] = new ImageView(this);
            adsDotViews[i].setImageResource(R.drawable.dot_inactive);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(size, size);
            p.setMargins(margin, 0, margin, 0);
            adsDots.addView(adsDotViews[i], p);
        }
        adsDots.setVisibility(adImages.size() > 1 ? View.VISIBLE : View.GONE);
        updateAdsDots(0);

        layoutAdsContainer.setVisibility(View.VISIBLE);

        adsHandler.removeCallbacks(adsRunnable);
        if (adImages.size() > 1) {
            int half = Integer.MAX_VALUE / 2;
            int start = half - (half % adImages.size());
            adsViewPager.setCurrentItem(start, false);
            adsHandler.postDelayed(adsRunnable, 3000);
        }
    }

    // ---------------------------------------------------------------------------------
    // Ads click tracking: API call first, redirect after
    // ---------------------------------------------------------------------------------
    private void setupAdsTapDetector() {
        View child = adsViewPager.getChildAt(0);
        if (!(child instanceof RecyclerView)) return;
        RecyclerView inner = (RecyclerView) child;

        final GestureDetector detector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        onAdClicked();
                        return true;
                    }
                });

        inner.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                detector.onTouchEvent(e);
                return false; // swipe ki disturb cheyadu
            }
        });
    }

    private RequestBody textPart(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value == null ? "" : value);
    }

    // Gson tho object nunchi field value teesukuntundi (field names tho compile problem radu)
    private String jsonField(Object obj, String... keys) {
        if (obj == null) return "";
        try {
            JsonObject o = new Gson().toJsonTree(obj).getAsJsonObject();
            for (String k : keys) {
                if (o.has(k) && !o.get(k).isJsonNull()) return o.get(k).getAsString();
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private void onAdClicked() {
        if (adClickInProgress || adImages == null || adImages.isEmpty()) return;

        int pos = adsViewPager.getCurrentItem() % adImages.size();
        AddImageResponse.AdItem ad = adImages.get(pos);

        LoginDataResponse.Result user =
                SharedPrefManager.getInstance(getApplicationContext()).getUserData();

        String imageId = jsonField(ad, "adimageId", "imageId", "image_id", "adId", "ad_id");
        final String redirectUrl = jsonField(ad, "adimageUrl", "link", "url", "redirectUrl", "redirect_url");

        String registerId = user != null ? user.getUserId() : "";
        String fullName = user != null
                ? (user.getUserFirstName() + " " + user.getUserLastName()).trim()
                : String.valueOf(txtName.getText());
        String mobile = jsonField(user, "userMobile", "user_mobile", "userMobileNumber",
                "mobileNumber", "mobile", "userPhone", "phone");

        Log.d("AD_CLICK", "imageId=" + imageId + " registerId=" + registerId
                + " fullName=" + fullName + " mobile=" + mobile + " url=" + redirectUrl);

        adClickInProgress = true;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(logging)
                .build();
        Retrofit rf = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface api = rf.create(APiInterface.class);

        api.userAdsClickTracking(
                textPart(imageId),
                textPart(registerId),
                textPart(fullName),
                textPart(mobile)
        ).enqueue(new Callback<AdClickResponse>() {
            @Override
            public void onResponse(Call<AdClickResponse> call, Response<AdClickResponse> response) {
                adClickInProgress = false;
                Log.d("AD_CLICK", "http=" + response.code()
                        + " body=" + new Gson().toJson(response.body()));
                openAdLink(redirectUrl);   // API tharuvatha redirect
            }

            @Override
            public void onFailure(Call<AdClickResponse> call, Throwable t) {
                adClickInProgress = false;
                Log.e("AD_CLICK", String.valueOf(t.getMessage()));
                openAdLink(redirectUrl);   // API fail ayina user ni redirect chestam
            }
        });
    }

    private void openAdLink(String url) {
        if (isFinishing() || isDestroyed()) return;
        if (url == null || url.trim().isEmpty()) return;
        LinkRouter.open(HomeActivity.this, url.trim());   // app:// -> inside app, otherwise browser
    }

    private void hideAds() {
        adImages = new ArrayList<>();
        adsHandler.removeCallbacks(adsRunnable);
        if (adsViewPager != null) adsViewPager.setAdapter(null);
        if (adsDots != null) adsDots.removeAllViews();
        layoutAdsContainer.setVisibility(View.GONE);
    }

    private void updateAdsDots(int position) {
        if (adsDotViews == null) return;
        for (int i = 0; i < adsDotViews.length; i++) {
            adsDotViews[i].setImageResource(i == position ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    private void bajanaSongsMethod() {
        // (unused) bajana songs section removed from home
    }

    // ---------------------------------------------------------------------------------
    // News
    // ---------------------------------------------------------------------------------
    private void newsMethod() {
        recyclerviewnews = findViewById(R.id.recycler_news);
        butAllNews = findViewById(R.id.viewall_but_news);
        newsList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerviewnews.setLayoutManager(layoutManager);

        newsfechedDatafromShredPreferences();
        butAllNews.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ViewAllNewsListActivity.class)));

        swipeRefreshLayout2 = findViewById(R.id.swipeRefreshLayout2);
        swipeRefreshLayout2.setOnRefreshListener(() -> {
            ayyappafetchDataFromDataBase();
        });
    }

    private void newsfechedDatafromShredPreferences() {
        newsfetchDataFromDataBase();

        List<NewsListModel> newsListModels = SharedPreferencesManager.getNewsList(HomeActivity.this);
        if (newsListModels != null && !newsListModels.isEmpty()) {
            updateRecyclerViewNews(newsListModels);
        } else {
            newsfetchDataFromDataBase();
        }
    }

    private void newsfetchDataFromDataBase() {
        if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface apiClient = retrofit.create(APiInterface.class);
        Call<NewsList> call = apiClient.getNewsList();
        call.enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                finishRefresh();

                NewsList newsList1 = response.body();
                newsList = new ArrayList<>(Arrays.asList(newsList1.getResult()));

                SharedPreferencesManager.saveNewsList(HomeActivity.this, newsList);

                updateRecyclerViewNews(newsList);
            }

            @Override
            public void onFailure(Call<NewsList> call, Throwable t) {
                finishRefresh();
                newsList = SharedPreferencesManager.getNewsList(HomeActivity.this);
                if (newsList != null && !newsList.isEmpty()) {
                    Log.d("Data Check", "NewsList size: " + newsList.size());
                    updateRecyclerViewNews(newsList);
                }
            }
        });
    }

    private void updateRecyclerViewNews(List<NewsListModel> newsListModels) {
        viewAllNewsListAdapter = new AllNewsListAdapter(HomeActivity.this, newsListModels);
        recyclerviewnews.setAdapter(viewAllNewsListAdapter);
    }

    // ---------------------------------------------------------------------------------
    // Ayyappa temples
    // ---------------------------------------------------------------------------------
    private void AyyaTemplesListMethod() {
        recyclerviewAyyappaTemples = findViewById(R.id.recycler_ayyappa_temples);
        viewallButton = findViewById(R.id.viewall_but);
        viewallButton.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ViewAllAyyappaTemplesActivity.class)));

        leftIconAyyappa = findViewById(R.id.leftIcon1);
        rightIconAyyappa = findViewById(R.id.rightIcon1);
        ayyaTempleListModels = new ArrayList<>();

        layoutManagerAyyappaTemple = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerviewAyyappaTemples.setLayoutManager(layoutManagerAyyappaTemple);

        ayyappafechedDatafromShredPreferences();

        leftIconAyyappa.setOnClickListener(v -> scrollRecyclerViewLeftayyappa());
        rightIconAyyappa.setOnClickListener(v -> scrollRecyclerViewRightayyappa());

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            ayyappafetchDataFromDataBase();
        });
    }

    private void scrollRecyclerViewRightayyappa() {
        if (ayyappaListAdapter == null) return;
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
        ayyappafetchDataFromDataBase();

        List<AyyaTempleListModel> templesListModels = SharedPreferencesManager.getAyyappaTemplesList(HomeActivity.this);
        if (templesListModels != null && !templesListModels.isEmpty()) {
            ayyappaupdateRecyclerView(templesListModels);
        }
    }

    private void ayyappafetchDataFromDataBase() {
        if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface apiClient = retrofit.create(APiInterface.class);
        Call<AyyappaTempleList> modelCall = apiClient.getAyyappaTempleList();
        modelCall.enqueue(new Callback<AyyappaTempleList>() {
            @Override
            public void onResponse(Call<AyyappaTempleList> call, Response<AyyappaTempleList> response) {
                finishRefresh();

                AyyappaTempleList list = response.body();
                ayyaTempleListModels = Arrays.asList(list.getResult());

                SharedPreferencesManager.saveAyyappaTempleList(HomeActivity.this, ayyaTempleListModels);

                ayyappaupdateRecyclerView(ayyaTempleListModels);
            }

            @Override
            public void onFailure(Call<AyyappaTempleList> call, Throwable t) {
                finishRefresh();
                ayyaTempleListModels = SharedPreferencesManager.getAyyappaTemplesList(HomeActivity.this);
                if (ayyaTempleListModels != null && !ayyaTempleListModels.isEmpty()) {
                    ayyappaupdateRecyclerView(ayyaTempleListModels);
                }
            }
        });
    }

    private void ayyappaupdateRecyclerView(List<AyyaTempleListModel> templesListModels) {
        List<AyyaTempleListModel> limitedList = templesListModels.size() > 10
                ? templesListModels.subList(0, 10)
                : templesListModels;

        ayyappaListAdapter = new AyyappaListAdapter(HomeActivity.this, limitedList);
        recyclerviewAyyappaTemples.setAdapter(ayyappaListAdapter);
    }

    // ---------------------------------------------------------------------------------
    // Temples
    // ---------------------------------------------------------------------------------
    private void TemplesListMethod() {
        recyclerviewTemples = findViewById(R.id.recycler_temples);

        butViewAll = findViewById(R.id.but_viewAll);
        butViewAll.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ViewAllTemplesActivity.class)));

        leftIcon = findViewById(R.id.leftIcon);
        rightIcon = findViewById(R.id.rightIcon);
        templeList = new ArrayList<>();

        layoutManagerTemple = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerviewTemples.setLayoutManager(layoutManagerTemple);

        fechedDatafromShredPreferences();

        leftIcon.setOnClickListener(v -> scrollRecyclerViewLeft());
        rightIcon.setOnClickListener(v -> scrollRecyclerViewRight());

        swipeRefreshLayout1 = findViewById(R.id.swipeRefreshLayout1);
        swipeRefreshLayout1.setOnRefreshListener(() -> {
            fetchDataFromDataBase();
        });
    }

    private void scrollRecyclerViewRight() {
        if (ayyappaTemplesListAdapter == null) return;
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
        fetchDataFromDataBase();
        List<TemplesListModel> templesListModels = SharedPreferencesManager.getTemplesList(HomeActivity.this);
        if (templesListModels != null && !templesListModels.isEmpty()) {
            updateRecyclerView(templesListModels);
        } else {
            fetchDataFromDataBase();
        }
    }

    private void fetchDataFromDataBase() {
        if (swipeRefreshLayout1 != null && !swipeRefreshLayout1.isRefreshing()) {
            swipeRefreshLayout1.setRefreshing(true);
        }
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface apiClient = retrofit.create(APiInterface.class);
        Call<TemplesList> modelCall = apiClient.getTempleList();
        modelCall.enqueue(new Callback<TemplesList>() {
            @Override
            public void onResponse(Call<TemplesList> call, Response<TemplesList> response) {
                finishRefresh();
                TemplesList list = response.body();
                templeList = new ArrayList<>(Arrays.asList(list.getResult()));

                SharedPreferencesManager.saveTempleList(HomeActivity.this, templeList);

                updateRecyclerView(templeList);
            }

            @Override
            public void onFailure(Call<TemplesList> call, Throwable t) {
                finishRefresh();
                templeList = SharedPreferencesManager.getTemplesList(HomeActivity.this);
                if (templeList != null && !templeList.isEmpty()) {
                    updateRecyclerView(templeList);
                }
            }
        });
    }

    private void updateRecyclerView(List<TemplesListModel> templesListModels) {
        List<TemplesListModel> limitedList = templesListModels.size() > 10
                ? templesListModels.subList(0, 10)
                : templesListModels;

        ayyappaTemplesListAdapter = new AyyappaTemplesListAdapter(HomeActivity.this, limitedList);
        recyclerviewTemples.setAdapter(ayyappaTemplesListAdapter);
    }

    // ---------------------------------------------------------------------------------
    // Seva (unused)
    // ---------------------------------------------------------------------------------
    private void sevaMethod() {
        // (unused) seva section removed from home
    }

    private void SevaDtailsMethod() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/")
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
                    sevaList = new ArrayList<>(Arrays.asList(data.getResult()));
                    SevaListModel modal = sevaList.get(0);
                    String profilePic = modal.getImage();
                    String imgUrl = "https://www.ayyappatelugu.com/assets/seva_samasthalu/" + profilePic;
                    String name = modal.getTitle();
                    String smalldiscription = modal.getSmalldescription();
                    String discription = modal.getDescription();

                    Intent intent = new Intent(HomeActivity.this, SevaDetailsActivity.class);
                    intent.putExtra("ItemName", name);
                    intent.putExtra("SmallDiscription", smalldiscription);
                    intent.putExtra("imagePath", imgUrl);
                    intent.putExtra("Discription", discription);
                    startActivity(intent);
                } else {
                    Log.e("API Response", "Unsuccessful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SevaList> call, Throwable t) {
                Log.e("API Failure", "Error: " + t.getMessage());
            }
        });
    }

    // ---------------------------------------------------------------------------------
    // Permissions
    // ---------------------------------------------------------------------------------
    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
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

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showUpdateNotification();
        }
    }

    private boolean areAllPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // ---------------------------------------------------------------------------------
    // Exit dialog
    // ---------------------------------------------------------------------------------
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
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
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

    // ---------------------------------------------------------------------------------
    // Navigation drawer
    // ---------------------------------------------------------------------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int action = item.getItemId();
        if (action == R.id.Ayyapa_karyam) {
            startActivity(new Intent(HomeActivity.this, AyyappaKaryamListActivity.class));

        } else if (action == R.id.guruswami_list) {
            startActivity(new Intent(HomeActivity.this, GuruSwamiListActivity.class));

        } else if (action == R.id.ayyappa_mandali) {
            startActivity(new Intent(HomeActivity.this, AyyappaMandaliListActivity.class));

        } else if (action == R.id.ayyappa_decration) {
            startActivity(new Intent(HomeActivity.this, AyyappaPetamListActivity.class));

        } else if (action == R.id.ayyappa_tours) {
            startActivity(new Intent(HomeActivity.this, AyyappaTourseDetailsACtivity.class));

        } else if (action == R.id.ayyappa_books) {
            startActivity(new Intent(HomeActivity.this, AyyapaBooksListActivity.class));

        } else if (action == R.id.ayyappa_details) {
            startActivity(new Intent(HomeActivity.this, AboutActivity.class));

        } else if (action == R.id.ayyappa_calender) {
            startActivity(new Intent(HomeActivity.this, CalenderActivity.class));

        } else if (action == R.id.ayyappa_blogs) {
            startActivity(new Intent(HomeActivity.this, BlogsActivity.class));

        } else if (action == R.id.ayyappa_Padayatra) {
            startActivity(new Intent(HomeActivity.this, PadayatraActivity.class));

        } else if (action == R.id.ayyappa_telugu_calender) {
            startActivity(new Intent(HomeActivity.this, TeluguCalenderActivity.class));

        } else if (action == R.id.menu_panchagam) {
            startActivity(new Intent(HomeActivity.this, PanchagamActivity.class));

        } else if (action == R.id.Ayyapa_bajana_songs) {
            startActivity(new Intent(HomeActivity.this, AyyappaBajanaSognsActivity.class));

        } else if (action == R.id.ayyappa_anadanam) {
            startActivity(new Intent(HomeActivity.this, AnadanamActivity.class));

        } else if (action == R.id.ayyappa_devalyalu) {
            startActivity(new Intent(HomeActivity.this, ViewAllTemplesActivity.class));

        } else if (action == R.id.ayyappa_ayyappadevlyalu) {
            startActivity(new Intent(HomeActivity.this, ViewAllAyyappaTemplesActivity.class));

        } else if (action == R.id.ayyappa_policy) {
            startActivity(new Intent(HomeActivity.this, PrivacyPolicyActivity.class));

        } else if (action == R.id.ayyappa_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));

        } else if (action == R.id.ayyappa_images) {
            Intent intent = new Intent(HomeActivity.this, ImagesListActivity.class);
            if (result != null) {
                intent.putExtra("userId", result.getUserId());
            }
            startActivity(intent);

        } else if (action == R.id.ayyappa_videos) {
            startActivity(new Intent(HomeActivity.this, PostVideosActivity.class));

        } else if (action == R.id.log_out) {
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    SharedPrefManager.getInstance(getApplicationContext()).logout();
                    finish();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // ---------------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startHeroAutoSlide();
        if (adsRunnable != null && adImages != null && adImages.size() > 1) {
            adsHandler.removeCallbacks(adsRunnable);
            adsHandler.postDelayed(adsRunnable, 3000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        adsHandler.removeCallbacks(adsRunnable);
        if (heroRunnable != null) heroHandler.removeCallbacks(heroRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (heroRunnable != null) heroHandler.removeCallbacks(heroRunnable);
        if (adsRunnable != null) adsHandler.removeCallbacks(adsRunnable);
        if (updateDialog != null && updateDialog.isShowing()) updateDialog.dismiss();
    }
}