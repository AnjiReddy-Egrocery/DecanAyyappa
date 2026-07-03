package com.dst.ayyapatelugu.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.dst.ayyapatelugu.Model.LoginDataResponse;
import com.dst.ayyapatelugu.Model.StudentUpdateProfile;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;

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

public class UploadDetailsActivity extends AppCompatActivity {

    ImageView imageProfile;
    ImageButton btnCamera;

    EditText edtName, edtDesigination;
    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 101;

    private Uri imageUri;
    private File imageFile;

    Button butUpload;
    String name, desigination;

    String userId;

    Toolbar toolbar;
    private static final int UCROP_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_details);

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


        imageProfile = findViewById(R.id.image_profile);
        edtName = findViewById(R.id.edt_first_name);
        edtDesigination = findViewById(R.id.edt_desigination);
        butUpload = findViewById(R.id.but_update_profile);
        btnCamera = findViewById(R.id.btn_camera);


        LoginDataResponse.Result result = SharedPrefManager.getInstance(getApplicationContext()).getUserData();
        userId = result.getUserId();

        Log.d("Reddy",userId);


        butUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = edtName.getText().toString();
                desigination = edtDesigination.getText().toString();

                updateMethod(userId,name,desigination,imageFile);


            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog or a menu to choose between camera or gallery
                showImagePickerDialog();
            }
        });

       btnCamera.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showImagePickerDialog();
           }
       });


    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadDetailsActivity.this);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            // 1. కెమెరా లేదా గ్యాలరీ నుండి ఇమేజ్ వచ్చినప్పుడు - క్రాపింగ్ స్టార్ట్ చేయండి
            if (requestCode == CAMERA_REQUEST || requestCode == GALLERY_REQUEST) {
                Uri sourceUri = (requestCode == CAMERA_REQUEST) ? imageUri : data.getData();
                startCrop(sourceUri);
            }
            // 2. క్రాపింగ్ పూర్తయ్యాక వచ్చే రిజల్ట్
            else if (requestCode == UCROP_REQUEST_CODE) {
                final Uri resultUri = com.yalantis.ucrop.UCrop.getOutput(data);
                if (resultUri != null) {
                    // ఇమేజ్ ని Glide తో డిస్‌ప్లే చేయడం
                    Glide.with(this)
                            .load(resultUri)
                            .placeholder(R.drawable.ayyapa_image)
                            .error(R.drawable.ayyapa_image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .signature(new ObjectKey(System.currentTimeMillis()))
                            .circleCrop()
                            .into(imageProfile);

                    // ఫైల్‌గా సేవ్ చేయడం (సర్వర్‌కి పంపడానికి)
                    saveCroppedImageToFile(resultUri);
                }
            }
        } else if (resultCode == com.yalantis.ucrop.UCrop.RESULT_ERROR) {
            final Throwable cropError = com.yalantis.ucrop.UCrop.getError(data);
            Toast.makeText(this, "Cropping Failed: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }

       /* if (resultCode == Activity.RESULT_OK) {
            try {
                Bitmap originalBitmap = null;

                if (requestCode == CAMERA_REQUEST) {
                    if (imageUri != null) {
                        originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        Log.d("ImagePicker", "Image captured from camera");
                    }
                } else if (requestCode == GALLERY_REQUEST && data != null) {
                    imageUri = data.getData();
                    if (imageUri != null) {
                        originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        Log.d("ImagePicker", "Image selected from gallery");
                    }
                }

                if (originalBitmap != null) {
                    int maxWidth = 1024;
                    int maxHeight = 1024;

                    float ratio = Math.min(
                            (float) maxWidth / originalBitmap.getWidth(),
                            (float) maxHeight / originalBitmap.getHeight()
                    );

                    int width = Math.round(ratio * originalBitmap.getWidth());
                    int height = Math.round(ratio * originalBitmap.getHeight());

                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);
                    Glide.with(this)
                            .load(resizedBitmap)
                            .placeholder(R.drawable.ayyapa_image) // loading time lo
                            .error(R.drawable.ayyapa_image)       // fail ayithe
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .signature(new ObjectKey(System.currentTimeMillis())) // forces fresh load
                            .circleCrop()
                            .into(imageProfile);

                    imageFile = new File(getCacheDir(), "resized_image.jpg");

                    try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                        fos.flush();
                        Log.d("ImagePicker", "Image resized and saved: " + imageFile.getAbsolutePath());
                    }

                } else {
                    Log.e("ImagePicker", "Bitmap is null");
                    Toast.makeText(UploadDetailsActivity.this, "Image load failed!", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(UploadDetailsActivity.this, "Image processing failed", Toast.LENGTH_SHORT).show();
            }
        }*/
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
    private void saveCroppedImageToFile(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            imageFile = new File(getCacheDir(), "final_image.jpg");
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMethod(String userId, String name, String desigination, File imageFile) {


        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
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
        RequestBody firstNamePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody middlenamePart = RequestBody.create(MediaType.parse("text/plain"), desigination);

        MultipartBody.Part imagePart = null;
        if (imageFile != null && imageFile.exists()) {
            RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
            imagePart = MultipartBody.Part.createFormData("picOnFlyer", imageFile.getName(), imageRequestBody);
        }

        Call<StudentUpdateProfile> call = apiClient.studentUpdatePost(
                idPart, firstNamePart, middlenamePart, imagePart
        );

        call.enqueue(new Callback<StudentUpdateProfile>() {
            @Override
            public void onResponse(Call<StudentUpdateProfile> call, Response<StudentUpdateProfile> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getResult() != null
                        && !response.body().getResult().isEmpty()) {

                    StudentUpdateProfile.FlyerInfoModel flyer =
                            response.body().getResult().get(0);



                    String flyerName = flyer.getNameOnFlyer();
                    String flyerDesignation = flyer.getDesignationOnFlyer();
                    String flyerPic = flyer.getPicOnFlyer();

                    Log.d("FLYER_NAME", flyerName);
                    Log.d("FLYER_DESIGNATION", flyerDesignation);
                    Log.d("FLYER_PIC", flyerPic);


                    SharedPrefManager.getInstance(UploadDetailsActivity.this)
                            .saveFlyerData(
                                    flyerName,
                                    flyerDesignation,
                                    response.body().getImageUrl() + flyerPic
                            );

                    // Images API already loaded ayyaka adapter ki pampandi
                    String fromActivity = getIntent().getStringExtra("from_activity");

                    Intent intent;
                    if (fromActivity != null && fromActivity.equals("post_videos")) {
                        // ఒకవేళ PostVideosActivity నుండి వస్తే అక్కడికే వెళ్తుంది (మీ క్లాస్ పేరు PostVideosActivity కాకపోతే మార్చుకోండి)
                        try {
                            intent = new Intent(UploadDetailsActivity.this, Class.forName("com.dst.ayyapatelugu.Activity.PostVideosActivity"));
                        } catch (ClassNotFoundException e) {
                            // క్లాస్ దొరకకపోతే సేఫ్ సైడ్ ImagesListActivity కి పంపుతుంది
                            intent = new Intent(UploadDetailsActivity.this, ImagesListActivity.class);
                        }
                    } else {
                        // డెఫాల్ట్ గా లేదా ImagesListActivity నుండి వస్తే ఇక్కడికి వెళ్తుంది
                        intent = new Intent(UploadDetailsActivity.this, ImagesListActivity.class);
                    }

                    intent.putExtra("flyer_name",
                           flyerName);

                    intent.putExtra("flyer_designation",
                            flyerDesignation);

                    intent.putExtra("flyer_pic",
                            flyerPic);

                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(
                            UploadDetailsActivity.this,
                            "No Flyer Data Found",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<StudentUpdateProfile> call, Throwable t) {

            }
        });


    }
}