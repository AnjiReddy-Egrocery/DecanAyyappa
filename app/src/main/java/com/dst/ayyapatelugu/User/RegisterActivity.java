package com.dst.ayyapatelugu.User;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.Model.SignUpWithGmail;
import com.dst.ayyapatelugu.Model.UserDataResponse;
import com.dst.ayyapatelugu.Model.VerifyUserDataResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;


import java.util.ArrayList;
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

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    LinearLayout layoutLogin;
    EditText edtFirstName, edtLastName, edtNumber, edtEmail, edtPassword, edtReenterPassword;
    Button butRegister;


    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_SIGN_UP_WITH_GOOGLE = 9002;


    private static final int CREDENTIAL_PICKER_REQUEST = 1001;
    private static final int REQUEST_PHONE_NUMBER_PERMISSION = 100;
    private static final int REQUEST_ACCOUNTS_PERMISSION = 1001;
    private static final int REQUEST_GOOGLE_SIGN_IN = 9001;

    AlertDialog loadingDialog;
    private int simRetryCount = 0;
    private static final int MAX_SIM_RETRY = 3;
    private static final int REQUEST_CODE_EMAIL_PICKER = 2001;
    private static final String GOOGLE_ACCOUNT_TYPE = "com.google";




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        edtFirstName = findViewById(R.id.edt_name);
        edtLastName = findViewById(R.id.edt_last_name);
        edtNumber = findViewById(R.id.edt_number);
        edtEmail = findViewById(R.id.edt_mail);
        edtPassword = findViewById(R.id.edt_password);
        edtReenterPassword = findViewById(R.id.edt_reenter_password);
        layoutLogin = findViewById(R.id.layout_login);
        butRegister = findViewById(R.id.but_register);
        //linearSignUpWithGmail=findViewById(R.id.layout_signup_gmail);


        Log.d("GoogleSignIn", "edtEmail initialized: " + (edtEmail != null));



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        edtNumber.setOnClickListener(new View.OnClickListener() {
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



        /*linearSignUpWithGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpWithGoogle(false);
            }
        });*/
        layoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        butRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtFirstName.getText().toString().trim();
                String lastname = edtLastName.getText().toString().trim();
                String number = edtNumber.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String reEnterPassword = edtReenterPassword.getText().toString().trim();


                if (!doPasswordsMatch(password, reEnterPassword)) {
                    Toast.makeText(RegisterActivity.this, "Password and ConfirmPassword do not match", Toast.LENGTH_SHORT).show();

                }

                if (isValidFirstName(name)
                        && isValidLastName(lastname)
                        && isValidEmail(email)
                        && isValidMobileNumber(number)
                        && isValidPassword(password)
                        && doPasswordsMatch(password, reEnterPassword)) {

                    validationMethod(name, lastname, number, email, password);
                    //Toast.makeText(RegisterActivity.this,"Data is Saved",Toast.LENGTH_LONG).show();
                }
            }
        });
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



    @SuppressLint("NewApi")
    // Fetch multiple SIM numbers
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


    // Show SIM selection dialog
    private void showSimSelectionDialog(ArrayList<String> simNumbers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                edtNumber.setFocusableInTouchMode(true);
                edtNumber.setFocusable(true);
                edtNumber.setCursorVisible(true);
                edtNumber.requestFocus();
                edtNumber.setText("");
                edtNumber.setHint("Enter your mobile number");
                Toast.makeText(this, "Please enter your number manually.", Toast.LENGTH_SHORT).show();
            } else {
                // Set the number and disable editing
                String[] parts = selected.split(": ");
                String selectedSimNumber = (parts.length > 1) ? parts[1].trim() : selected;
                edtNumber.setText(selectedSimNumber);
                edtNumber.setFocusable(false);
                edtNumber.setCursorVisible(false);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    edtNumber.setText(credential.getId());  // Display the retrieved number
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



    public void ShowHidePass(View view) {

        if(view.getId()==R.id.show_pass_btn){
            if(edtPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.visiablityoff);
                //Show Password
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.visiablity);
                //Hide Password
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    public void ShowHideConfirmPass(View view) {

        if(view.getId()==R.id.show_Confirmpass_btn){
            if(edtReenterPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.visiablityoff);
                //Show Password
                edtReenterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.visiablity);
                //Hide Password
                edtReenterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    private void signUpWithGoogle(boolean isSignUp) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, isSignUp ? RC_SIGN_UP_WITH_GOOGLE : RC_SIGN_IN);
    }

    private boolean doPasswordsMatch(String password, String reEnterPassword) {


        return password.equals(reEnterPassword);
    }

    private boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this,"Please enter your password",Toast.LENGTH_SHORT).show();
            return false;
        }
        return password.length() >= 6;
    }

    private boolean isValidMobileNumber(String number) {
        if (TextUtils.isEmpty(number)){
            Toast.makeText(RegisterActivity.this,"Please enter your mobile number",Toast.LENGTH_SHORT).show();
            return false;
        }
        return Patterns.PHONE.matcher(number).matches();
    }

    private boolean isValidLastName(String lastname) {
         if (TextUtils.isEmpty(lastname)){
             Toast.makeText(RegisterActivity.this,"Please enter your last name",Toast.LENGTH_SHORT).show();
             return false;
         }
         return true;

    }

    private boolean isValidFirstName(String name) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this,"Please Enter Your emailId",Toast.LENGTH_SHORT).show();
        }
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private void validationMethod(String name, String lastname, String number, String email, String password) {
        Log.d("RegisterActivity", "Validation method called with: " +
                "Name: " + name +
                ", Lastname: " + lastname +
                ", Number: " + number +
                ", Email: " + email +
                ", Password: " + password);
        //progressBar.setVisibility(View.VISIBLE);
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
        RequestBody firstnamePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody lastnamePart = RequestBody.create(MediaType.parse("text/plain"), lastname);
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody mobileNumberPart = RequestBody.create(MediaType.parse("text/plain"), number);
        RequestBody pwdPart = RequestBody.create(MediaType.parse("text/plain"), password);
        Log.d("RegisterActivity", "Prepared request body for API call.");
        Call<UserDataResponse> call = apiClient.postData(firstnamePart, lastnamePart, emailPart, mobileNumberPart, pwdPart);
        call.enqueue(new Callback<UserDataResponse>() {
            @Override
            public void onResponse(Call<UserDataResponse> call, Response<UserDataResponse> response) {
                Log.d("RegisterActivity", "Response received: " + response.toString());

                if (response.isSuccessful()) {
                    UserDataResponse userDataResponse = response.body();
                    if (userDataResponse != null) { // Check for null response
                        Log.d("RegisterActivity", "UserDataResponse received with error code: " + userDataResponse.getErrorCode());

                        if (userDataResponse.getErrorCode().equals("203")) {
                            Toast.makeText(RegisterActivity.this, "Email and Mobile Number already exist", Toast.LENGTH_SHORT).show();
                        } else if (userDataResponse.getErrorCode().equals("200")) {
                            String registerId = "";
                            String otp = "";
                            List<UserDataResponse.Result> list = userDataResponse.getResult();
                            for (UserDataResponse.Result result : list) {
                                registerId = result.getRegisterId();
                                otp = result.getOtp();
                            }
                            Log.d("RegisterActivity", "Registration successful. Register ID: " + registerId + ", OTP: " + otp);
                            Toast.makeText(RegisterActivity.this, "User Registration Completed Successfully", Toast.LENGTH_LONG).show();
                            conformationDialog(registerId, otp);
                        }
                    } else {
                        Log.e("RegisterActivity", "UserDataResponse is null.");
                        Toast.makeText(RegisterActivity.this, "Data Error: No response received", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("RegisterActivity", "Response error: " + response.errorBody().toString());
                    Toast.makeText(RegisterActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserDataResponse> call, Throwable t) {
                Log.e("RegisterActivity", "Network Error: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void conformationDialog(String registerId, String otp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirmation, null);
        builder.setView(dialogView);

        CheckBox checkBox = dialogView.findViewById(R.id.simpleCheckBox);
        Button cancelButton = dialogView.findViewById(R.id.but_cancel);
        Button acceptButton = dialogView.findViewById(R.id.but_accept);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Enable or disable the "Accept" button based on checkbox state
            acceptButton.setEnabled(isChecked);
        });

        // Set up listener for the "Accept" button
        acceptButton.setOnClickListener(v -> {
            // Check if the user has agreed to the privacy policy
            if (checkBox.isChecked()) {
                otpVerfication(registerId,otp);
            } else {
                // User didn't agree, show a message or take appropriate action
                Toast.makeText(this, "Please agree to the privacy policy", Toast.LENGTH_SHORT).show();
            }

            // Dismiss the dialog
            builder.create().dismiss();
        });

        // Set up listener for the "Cancel" button
        cancelButton.setOnClickListener(v -> {
            // Perform any necessary actions on cancel button click
            // For example, dismiss the dialog
            builder.create().dismiss();
        });

        // Show the dialog
        builder.create().show();
    }

    private void otpVerfication(String registerId, String otp) {
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
        RequestBody registerIdPart = RequestBody.create(MediaType.parse("text/plain"), registerId);
        RequestBody otpPart = RequestBody.create(MediaType.parse("text/plain"), otp);

        Call<VerifyUserDataResponse> call = apiClient.verifyData(registerIdPart,otpPart);
        call.enqueue(new Callback<VerifyUserDataResponse>() {
            @Override
            public void onResponse(Call<VerifyUserDataResponse> call, Response<VerifyUserDataResponse> response) {
                if (response.isSuccessful()){
                    VerifyUserDataResponse verifyUserDataResponse=response.body();
                    if (verifyUserDataResponse.getErrorCode().equals("201")){
                        Toast.makeText(RegisterActivity.this,"Enter OTP is Invalid",Toast.LENGTH_LONG).show();
                    }else if (verifyUserDataResponse.getErrorCode().equals("200")){
                        Toast.makeText(RegisterActivity.this, "User Registration Completed Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<VerifyUserDataResponse> call, Throwable t) {

            }
        });
    }
    private void performSignUp(GoogleSignInAccount account) {
        // Extract information from the account and perform sign-up

        String displayName = account.getDisplayName();
        String email = account.getEmail();
        String profilepic = String.valueOf(account.getPhotoUrl());

        // Now, send this information to your server for user registration
        sendSignUpDataToServer(displayName, email, profilepic);
    }

    private void sendSignUpDataToServer(String displayName, String email, String profilepic) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface apiClient = retrofit.create(APiInterface.class);

        RequestBody displayPar = RequestBody.create(MediaType.parse("text/plain"), displayName);
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody photoPart = RequestBody.create(MediaType.parse("text/plain"), profilepic);

        Call<SignUpWithGmail> call = apiClient.PostSignUp(displayPar, emailPart, photoPart);
        call.enqueue(new Callback<SignUpWithGmail>() {
            @Override
            public void onResponse(Call<SignUpWithGmail> call, Response<SignUpWithGmail> response) {
                if (response.isSuccessful()) {

                    SignUpWithGmail signUpWithGmail = response.body();
                    if (signUpWithGmail.getErrorCode().equals("200")) {
                        String displayName = "";
                        String email = "";
                        String photo = "";
                        List<SignUpWithGmail.Result> results = signUpWithGmail.getResult();
                        for (int i = 0; i < results.size(); i++) {
                            displayName = results.get(i).getFullName();
                            email = results.get(i).getUserEmail();
                            photo = results.get(i).getProfilePic();

                            Log.e("Reddy", "displayname: " + displayName);
                            Log.e("Reddy", "email: " + email);
                            Log.e("Reddy", "photo: " + photo);

                        }

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra("displayname", displayName);
                        intent.putExtra("email", email);
                        intent.putExtra("profilepic", photo);
                        startActivity(intent);


                    }

                } else {
                    Toast.makeText(RegisterActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<SignUpWithGmail> call, Throwable t) {

            }
        });
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Connection failed, show error message
        Toast.makeText(RegisterActivity.this, "Connection to Google Play services failed. Please try again.", Toast.LENGTH_LONG).show();
    }




}


