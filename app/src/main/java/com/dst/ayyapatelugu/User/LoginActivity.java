package com.dst.ayyapatelugu.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.DataBase.SharedPrefManager;
import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.Model.LoginDataResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.LocationForegroundService;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    TextView txtCreateAccount, txtFpwd;

    EditText edtEmail, edtMobile;
    Button butLogin;
    boolean isAllFieldsChecked = false;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    boolean isLoggedIn;
    //LinearLayout linearAuth;

    private int simRetryCount = 0;
    private static final int MAX_SIM_RETRY = 3;
    private static final int REQUEST_CODE_EMAIL_PICKER = 2001;
    private static final String GOOGLE_ACCOUNT_TYPE = "com.google";
    private static final int CREDENTIAL_PICKER_REQUEST = 1001;
    private static final int REQUEST_PHONE_NUMBER_PERMISSION = 100;
    AlertDialog loadingDialog;
    ProgressDialog progressDialog;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtCreateAccount = findViewById(R.id.txt_create);
        txtFpwd = findViewById(R.id.txt_fwd);
        edtEmail = findViewById(R.id.edt_email);
        edtMobile = findViewById(R.id.edt_mobile);
        butLogin = findViewById(R.id.but_login);

        txtCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intentRegister);
            }
        });

        txtFpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentforgot = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intentforgot);
            }
        });

        edtMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermissions();
            }
        });

        edtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndShowAccounts();
            }
        });

        butLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                String mobile = edtMobile.getText().toString().trim();

                String loginInput = "";

                if (!email.isEmpty()) {

                    loginInput = email;

                } else if (!mobile.isEmpty()) {

                    loginInput = mobile;

                }


                if (!loginInput.isEmpty()) {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Loading Please wait ......");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    LoginMethod(loginInput);

                } else {

                    Toast.makeText(
                            LoginActivity.this,
                            "Please Enter Email or Mobile Number",
                            Toast.LENGTH_SHORT
                    ).show();

                }
            }
        });





        askLocationPermission();

        //linearAuth = findViewById(R.id.layout_auth);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(LoginActivity.this, gso);

        /*linearAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });*/
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        isLoggedIn = SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn();
        if (account != null || isLoggedIn) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkPermissionAndShowAccounts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.GET_ACCOUNTS}, 123);
        } else {
            showAccountPicker();
        }
    }

    private void showAccountPicker() {
        Intent intent = AccountManager.newChooseAccountIntent(
                null, null,
                new String[]{GOOGLE_ACCOUNT_TYPE},
                false, null, null, null, null
        );
        startActivityForResult(intent, REQUEST_CODE_EMAIL_PICKER);
    }

    private void checkAndRequestPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Check both permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_PHONE_NUMBERS,
                                Manifest.permission.READ_PHONE_STATE
                        }, REQUEST_PHONE_NUMBER_PERMISSION);
            } else {
                getSimNumbers();
            }
        }
    }
    @SuppressLint("NewApi")
    private void getSimNumbers() {
        showLoadingDialog(); // 👈 show loader

        ArrayList<String> simNumbers = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
                != PackageManager.PERMISSION_GRANTED) {
            hideLoadingDialog();
            return;
        }

        SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

        if (subscriptionInfoList == null || subscriptionInfoList.isEmpty()) {
            if (simRetryCount < MAX_SIM_RETRY) {
                simRetryCount++;
                new Handler(Looper.getMainLooper()).postDelayed(this::getSimNumbers, 2000);
            } else {
                hideLoadingDialog(); // 👈 hide if failed
                Toast.makeText(this, "Unable to fetch SIM details.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        simRetryCount = 0;
        for (SubscriptionInfo info : subscriptionInfoList) {
            String phoneNumber = info.getNumber();
            int simSlot = info.getSimSlotIndex();
            String carrierName = info.getCarrierName().toString();

            if (phoneNumber == null || phoneNumber.isEmpty()) {
                phoneNumber = "Number not available";
            }

            simNumbers.add("SIM " + (simSlot + 1) + " (" + carrierName + "): " + phoneNumber);
        }

        hideLoadingDialog(); // 👈 hide after success

        if (!simNumbers.isEmpty()) {
            showSimSelectionDialog(simNumbers);
        }
    }

    private void showSimSelectionDialog(ArrayList<String> simNumbers) {
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select SIM Number");

        boolean hasUnavailableNumber = false;

        // Check if any SIM has "Number not available"
        for (String sim : simNumbers) {
            if (sim.contains("Number not available")) {
                hasUnavailableNumber = true;
                break;
            }
        }

        // Only add "Enter manually" if any number is not available
        if (hasUnavailableNumber) {
            simNumbers.add("Enter manually");
        }

        String[] simArray = simNumbers.toArray(new String[0]);

        builder.setItems(simArray, (dialog, which) -> {
            String selected = simNumbers.get(which);

            if (selected.equals("Enter manually")) {
                // Enable manual input
                edtMobile.setFocusableInTouchMode(true);
                edtMobile.setFocusable(true);
                edtMobile.setCursorVisible(true);
                edtMobile.requestFocus();
                edtMobile.setText("");
                edtMobile.setHint("Enter your mobile number");
                Toast.makeText(this, "Please enter your number manually.", Toast.LENGTH_SHORT).show();
            } else {
                // Extract number
                String[] parts = selected.split(": ");
                String selectedSimNumber = (parts.length > 1) ? parts[1].trim() : selected;

                // ✅ Remove +91 or 91 prefix automatically
                if (selectedSimNumber.startsWith("+91")) {
                    selectedSimNumber = selectedSimNumber.substring(3);
                } else if (selectedSimNumber.startsWith("91") && selectedSimNumber.length() > 10) {
                    selectedSimNumber = selectedSimNumber.substring(2);
                }

                // ✅ Ensure it’s exactly 10 digits
                if (selectedSimNumber.length() == 10 && selectedSimNumber.matches("\\d{10}")) {
                    edtMobile.setText(selectedSimNumber);
                    edtMobile.setFocusable(false);
                    edtMobile.setCursorVisible(false);
                } else {
                    edtMobile.setFocusableInTouchMode(true);
                    edtMobile.setFocusable(true);
                    edtMobile.setCursorVisible(true);
                    edtMobile.requestFocus();
                    edtMobile.setText("");
                    edtMobile.setHint("Enter your mobile number");
                    Toast.makeText(this, "Invalid SIM number detected. Please enter manually.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_loading); // we'll create this layout next
        builder.setCancelable(false);
        loadingDialog = builder.create();
        loadingDialog.show();
    }
    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

//    public void ShowHidePass(View view) {
//
//        int cursorPosition = edtPassword.getSelectionStart();
//
//        if (edtPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
//            ((ImageView) (view)).setImageResource(R.drawable.visiablityoff);
//            // Show Password
//            edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//        } else {
//            ((ImageView) (view)).setImageResource(R.drawable.visiablity);
//            // Hide Password
//            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//        }
//
//        // Restore the cursor position
//        edtPassword.setSelection(cursorPosition);
//    }


    private void startLocationTracking() {

        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            return;
        }

        Intent serviceIntent =
                new Intent(
                        this,
                        LocationForegroundService.class
                );

        ContextCompat.startForegroundService(
                this,
                serviceIntent
        );
    }

    private void askLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    200
            );

        } else {

            askBackgroundPermission();
        }
    }

    private void askBackgroundPermission() {

        if(Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.Q){

            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        },
                        101
                );

            } else {

                startLocationTracking();
            }

        } else {

            startLocationTracking();
        }
    }


    private void LoginMethod(String parentEmail) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true) // Bypasses hostname verification
                .addInterceptor(loggingInterceptor)
                .build();
        // Create the Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
                .client(client)
                .build();

        // Create the API interface
        APiInterface apiClient = retrofit.create(APiInterface.class);

        // Create request body parts for email and password
        RequestBody parentEmailPart = RequestBody.create(MediaType.parse("text/plain"), parentEmail);


        // Call the login API
        Call<LoginDataResponse> call = apiClient.LoginData(parentEmailPart);

        // Enqueue the API call to execute asynchronously
        call.enqueue(new Callback<LoginDataResponse>() {
            @Override
            public void onResponse(Call<LoginDataResponse> call, Response<LoginDataResponse> response) {
                // Check if the response was successful and not null
                if (response.isSuccessful() && response.body() != null) {
                    LoginDataResponse dataResponse = response.body();

                    String errorCode = dataResponse.getErrorCode();
                    if (errorCode.equals("201")) {
                        // Incorrect email or password
                        Toast.makeText(LoginActivity.this, "The user data not found with the provided data. ", Toast.LENGTH_LONG).show();
                    } else if (errorCode.equals("200")) {

                        if(progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }

                        // Successful login, save data and navigate to HomeActivity
                        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true); // Save login state
                        editor.apply();

                        // Save additional user data if needed
                        SharedPrefManager.getInstance(getApplicationContext()).insertData(dataResponse);
                        Toast.makeText(LoginActivity.this, "User Login Successfully", Toast.LENGTH_SHORT).show();


                        // Navigate to HomeActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish(); // Close LoginActivity
                    }
                } else {
                    // Handle unexpected response (e.g., server error or invalid data)
                    Log.e("LoginError", "Response code: " + response.code());
                    Toast.makeText(LoginActivity.this, "Login failed. Unexpected response.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginDataResponse> call, Throwable t) {

                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                // Handle network errors, including SSLHandshakeException
                if (t instanceof SSLHandshakeException) {
                    Log.e("LoginError", "SSL handshake failed: " + t.getMessage());
                    Toast.makeText(LoginActivity.this, "SSL Error. Please check your network security.", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("LoginError", "Network error: " + t.getMessage());
                    Toast.makeText(LoginActivity.this, "Login failed. Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }



    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private boolean isValidEmail(String parentEmail) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return parentEmail.matches(emailPattern);
    }

    private void SignIn() {

        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    edtMobile.setText(credential.getId());  // Display the retrieved number
                }
            }
        }

        if (requestCode == REQUEST_CODE_EMAIL_PICKER && resultCode == RESULT_OK) {
            if (data != null) {
                String selectedEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (selectedEmail != null) {
                    edtEmail.setText(selectedEmail);
                }
            }
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
                grantResults
        );

        if(requestCode == 200){

            if(grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED){

                askBackgroundPermission();
            }
        }

        if(requestCode == 101){

            startLocationTracking();
        }

        if (requestCode == REQUEST_PHONE_NUMBER_PERMISSION) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                getSimNumbers();
            } else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 123 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showAccountPicker();
        }
    }




}

