package com.dst.ayyapatelugu.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.dst.ayyapatelugu.DataBase.SharedPrefManager;
import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.Model.LoginDataResponse;
import com.dst.ayyapatelugu.Model.StudentUpdateProfile;
import com.dst.ayyapatelugu.Model.UserUpdateProfile;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {
    Toolbar toolbar;

    EditText edtName, edtLastName, edtEmail, edtMobile;
    EditText edtFlyerName, edtDesignation;
    ImageView imageProfile, imageFlyer;
    ImageButton imageprofile,imageflyer;

    Button butSubmit;

    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 101;

    private Uri imageUri;
    private File imageFile;
    private boolean isFlyerImage = false;
    private File profileImageFile;
    private File flyerImageFile;

    String firstname, lastname, email,mobile,flyername,flyerdesigination;

    String userId;
    private static final int UCROP_REQUEST_CODE = 102;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbar);
       /* toolbar.setLogo(R.drawable.user_profile_background);
        toolbar.setTitle("  మా గురించి ");
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

        LoginDataResponse.Result result = SharedPrefManager.getInstance(getApplicationContext()).getUserData();
        userId = result.getUserId();

        Log.d("Reddy",userId);


        imageflyer = findViewById(R.id.btn_flyer_camera);
        imageprofile = findViewById(R.id.btn_camera);

        imageflyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFlyerImage = true;
                showImagePickerDialog();
            }
        });

        imageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFlyerImage = false;
                showImagePickerDialog();
            }
        });

        initViews();
        loadLocalData();
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Select Image From");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                checkCameraPermissionAndOpenCamera();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        } else {
            openCamera(); // Permission already granted
        }
    }

    private void openCamera() {
        Intent cameraIntent =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = createImageFile();

            if (photoFile == null) {
                Toast.makeText(
                        this,
                        "Unable to create image file",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            imageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    photoFile
            );

            cameraIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    imageUri
            );

            startActivityForResult(
                    cameraIntent,
                    CAMERA_REQUEST
            );
        }
    }

    private File createImageFile() {
        String fileName = "IMG_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(fileName, ".jpg", storageDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults);

        if (requestCode == CAMERA_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                openCamera();

            } else {

                Toast.makeText(
                        this,
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            // కెమెరా లేదా గ్యాలరీ నుండి వచ్చినప్పుడు క్రాపింగ్ స్టార్ట్ చేయండి
            if (requestCode == CAMERA_REQUEST || requestCode == GALLERY_REQUEST) {
                Uri sourceUri = (requestCode == CAMERA_REQUEST) ? imageUri : data.getData();
                startCrop(sourceUri);
            }
            // క్రాపింగ్ పూర్తయ్యాక రిజల్ట్ తీసుకోవడం
            else if (requestCode == UCROP_REQUEST_CODE) {
                final Uri resultUri = com.yalantis.ucrop.UCrop.getOutput(data);
                if (resultUri != null) {
                    handleCroppedImage(resultUri);
                }
            }
        }
    }

    private void startCrop(Uri uri) {
        // 1. అవుట్‌పుట్ ఫైల్ నేమ్ క్రియేట్ చేయడం
        String destinationFileName = "cropped_" + System.currentTimeMillis() + ".jpg";

        // 2. UCrop ఇన్‌స్టాన్స్ క్రియేట్ చేయడం
        com.yalantis.ucrop.UCrop uCrop = com.yalantis.ucrop.UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));

        // 3. UCrop ఆప్షన్స్ సెట్ చేయడం
        com.yalantis.ucrop.UCrop.Options options = new com.yalantis.ucrop.UCrop.Options();

        // థీమ్ మరియు రంగులు
        options.setToolbarColor(android.graphics.Color.parseColor("#FF6600")); // టూల్ బార్ కలర్
        options.setStatusBarColor(android.graphics.Color.parseColor("#E65100")); // స్టేటస్ బార్ కలర్
        options.setActiveControlsWidgetColor(android.graphics.Color.parseColor("#FF6600")); // బటన్స్ హైలైట్ కలర్
        options.setToolbarWidgetColor(android.graphics.Color.WHITE); // టూల్ బార్ ఐకాన్స్ కలర్

        // క్రాపింగ్ సెట్టింగ్స్
        options.setCircleDimmedLayer(true); // రౌండ్ క్రాపింగ్ కోసం
        options.setShowCropGrid(true);      // గ్రిడ్ లైన్స్ చూపించడానికి
        options.setFreeStyleCropEnabled(true); // యూజర్ ఫ్రీగా క్రాప్ చేయడానికి

        // ఫుల్ స్క్రీన్ కోసం లేదా క్రాపింగ్ స్పేస్ కోసం
        options.setHideBottomControls(false); // కింది ఆప్షన్స్ (Rotate/Crop) చూపించడానికి

        // 4. యాస్పెక్ట్ రేషియో సెట్ చేయడం (స్క్వేర్ కోసం 1:1)
        uCrop.withAspectRatio(1, 1);

        // 5. ఆప్షన్స్ అప్లై చేసి స్టార్ట్ చేయడం
        uCrop.withOptions(options);
        uCrop.start(this, UCROP_REQUEST_CODE);
    }
    private void handleCroppedImage(Uri resultUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
            File file = new File(getCacheDir(), (isFlyerImage ? "flyer_" : "profile_") + System.currentTimeMillis() + ".jpg");

            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
            }

            // ఇమేజ్ ప్రివ్యూ మరియు ఫైల్ సేవ్
            if (isFlyerImage) {
                flyerImageFile = file;
                Glide.with(this).load(resultUri).circleCrop().into(imageFlyer);
            } else {
                profileImageFile = file;
                Glide.with(this).load(resultUri).circleCrop().into(imageProfile);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLocalData() {
        SharedPrefManager sp = SharedPrefManager.getInstance(this);

        // BASIC INFO
        edtName.setText(sp.getUserData().getUserFirstName() != null ? sp.getUserData().getUserFirstName() : "");
        edtLastName.setText(sp.getUserData().getUserLastName() != null ? sp.getUserData().getUserLastName() : "");
        edtEmail.setText(sp.getUserData().getUserEmail() != null ? sp.getUserData().getUserEmail() : "");
        edtMobile.setText(sp.getUserData().getUserMobile() != null ? sp.getUserData().getUserMobile() : "");

        // FLYER INFO
        edtFlyerName.setText(sp.getFlyerName() != null ? sp.getFlyerName() : "");
        edtDesignation.setText(sp.getFlyerDesignation() != null ? sp.getFlyerDesignation() : "");

        // IMAGES


        String profilePic = sp.getProfilePic();

        Log.d("PROFILE_PIC", profilePic);

        Glide.with(this)
                .load(profilePic)
                .placeholder(R.drawable.ayyappaphome)
                .error(R.drawable.ayyappaphome)
                .circleCrop()
                .into(imageProfile);

// Flyer Image
        String pic = sp.getFlyerPic();

        Log.d("FLYER_URL", pic);

        Glide.with(this)
                .load(pic)
                .placeholder(R.drawable.ayyappaphome)
                .error(R.drawable.ayyappaphome)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .circleCrop()
                .into(imageFlyer);

        Log.d("Reddy","LOAD_PROFILE"+ profilePic);
        Log.d("Reddy","LOAD_FLYER"+ pic);


    }

    private void initViews() {

        edtName = findViewById(R.id.edt_name);
        edtLastName = findViewById(R.id.edt_last_name);
        edtEmail = findViewById(R.id.edt_mail);
        edtMobile = findViewById(R.id.edt_number);
        imageProfile = findViewById(R.id.image_profile);

        // FLYER INFO
        edtFlyerName = findViewById(R.id.edt_first_name);
        edtDesignation = findViewById(R.id.edt_desigination);
        imageFlyer = findViewById(R.id.image_flyer);

        butSubmit = findViewById(R.id.but_update_profile);

        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                       firstname = edtName.getText().toString();
                       lastname = edtLastName.getText().toString();
                       email = edtEmail.getText().toString();
                       mobile = edtMobile.getText().toString();

                       flyername = edtFlyerName.getText().toString();
                       flyerdesigination = edtDesignation.getText().toString();

                       UpdateMethod(userId,firstname,lastname,email,mobile,flyername,flyerdesigination,profileImageFile,flyerImageFile);
            }
        });
    }

    private void UpdateMethod(String userId, String firstname, String lastname, String email, String mobile, String flyername, String flyerdesigination, File profileImageFile, File flyerImageFile) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface apiClient = retrofit.create(APiInterface.class);

        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody firstNamePart = RequestBody.create(MediaType.parse("text/plain"), firstname);
        RequestBody lastnamePart = RequestBody.create(MediaType.parse("text/plain"), lastname);
        RequestBody mobilePart = RequestBody.create(MediaType.parse("text/plain"), mobile);
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody flyernamePart = RequestBody.create(MediaType.parse("text/plain"), flyername);
        RequestBody desiginationPart = RequestBody.create(MediaType.parse("text/plain"), flyerdesigination);
        MultipartBody.Part imagePart = null;

        if (flyerImageFile != null && flyerImageFile.exists()) {

            RequestBody imageRequestBody =
                    RequestBody.create(
                            MediaType.parse("image/jpeg"),
                            flyerImageFile
                    );

            imagePart = MultipartBody.Part.createFormData(
                    "picOnFlyer",
                    flyerImageFile.getName(),
                    imageRequestBody
            );
        }
        MultipartBody.Part profilePart = null;

        if (profileImageFile != null && profileImageFile.exists()) {

            RequestBody profileBody =
                    RequestBody.create(
                            MediaType.parse("image/jpeg"),
                            profileImageFile
                    );

            profilePart = MultipartBody.Part.createFormData(
                    "profilePic",
                    profileImageFile.getName(),
                    profileBody
            );
        }
        Call<UserUpdateProfile> call = apiClient.userUpdatePost(
                idPart, firstNamePart, lastnamePart,mobilePart,emailPart,flyernamePart,desiginationPart, profilePart,imagePart
        );

        call.enqueue(new Callback<UserUpdateProfile>() {
            @Override
            public void onResponse(Call<UserUpdateProfile> call, Response<UserUpdateProfile> response) {

                Log.d("API_CODE", "" + response.code());
                Log.d("API_CODE", "" + response.body());

                if (response.body() != null) {
                    Log.d("API_CODE", "" + response.body().getResult());
                }

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getResult() != null
                        && !response.body().getResult().isEmpty()) {

                    UserUpdateProfile.UserInfoModel flyer =
                            response.body().getResult().get(0);

                    String name = flyer.getFullName();
                    String mobile = flyer.getUserMobile();
                    String email= flyer.getUserEmail();
                    String profilepic = flyer.getProfilePic();

                    String flyerName = flyer.getNameOnFlyer();
                    String flyerDesignation = flyer.getDesignationOnFlyer();
                    String flyerPic = flyer.getPicOnFlyer();

                    Log.d("FLYER_NAME", flyerName);
                    Log.d("FLYER_DESIGNATION", flyerDesignation);
                    Log.d("FLYER_PIC", flyerPic);

                    SharedPrefManager sp =
                            SharedPrefManager.getInstance(ProfileActivity.this);

// ===== Update User Details =====
                    sp.updateUserData(
                            firstname,
                            lastname,
                            email,
                            mobile
                    );

// ===== Flyer Image Save =====
                    String oldFlyerPic = sp.getFlyerPic();

                    if (flyerPic != null && !flyerPic.trim().isEmpty()) {

                        oldFlyerPic =
                                "https://www.ayyappatelugu.com/public/assets/user_images/"
                                        + flyerPic;
                    }

                    sp.saveFlyerData(
                            flyerName,
                            flyerDesignation,
                            oldFlyerPic
                    );

// ===== Profile Image Save =====
                    String oldProfilePic = sp.getProfilePic();

                    if (profilepic != null && !profilepic.trim().isEmpty()) {

                        oldProfilePic =
                                "https://www.ayyappatelugu.com/public/assets/user_images/"
                                        + profilepic;
                    }

                    sp.saveProfilePic(oldProfilePic);

                    Log.d("Reddy","PROFILE_URL"+ oldProfilePic);
                    Log.d("Reddy","FLYER_URL"+ oldFlyerPic);
                    // Images API already loaded ayyaka adapter ki pampandi
                    Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Log.e("API_CODE", "Result empty or null");
                    Toast.makeText(
                            ProfileActivity.this,
                            "No Flyer Data Found",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<UserUpdateProfile> call, Throwable t) {

            }
        });
    }
}