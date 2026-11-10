package com.dst.ayyapatelugu.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dst.ayyapatelugu.Model.SignUpWithGmail;
import com.dst.ayyapatelugu.Model.UserDataResponse;
import com.dst.ayyapatelugu.Model.VerifyUserDataResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

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
    EditText edtFirstName, edtLastName, edtNumber, edtEmail;
    Button butRegister;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_SIGN_UP_WITH_GOOGLE = 9002;

    private static final int CREDENTIAL_PICKER_REQUEST = 1001;
    private static final int REQUEST_PHONE_NUMBER_PERMISSION = 100;
    private static final int REQUEST_GET_ACCOUNTS_PERMISSION = 123;
    private static final int REQUEST_CODE_EMAIL_PICKER = 2001;
    private static final String GOOGLE_ACCOUNT_TYPE = "com.google";
    private static final int MAX_SIM_RETRY = 3;

    AlertDialog loadingDialog;
    private int simRetryCount = 0;

    // Android app => 0
    final String isIOS = "0";

    // request already running ayite malli pampakunda
    private boolean isRequestInProgress = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtFirstName = findViewById(R.id.edt_name);
        edtLastName = findViewById(R.id.edt_last_name);
        edtNumber = findViewById(R.id.edt_number);
        edtEmail = findViewById(R.id.edt_mail);

        layoutLogin = findViewById(R.id.layout_login);
        butRegister = findViewById(R.id.but_register);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        edtNumber.setOnClickListener(view -> {
            clearFocusAndHideKeyboard();
            checkAndRequestPermissions();
        });

        edtEmail.setOnClickListener(v -> {
            clearFocusAndHideKeyboard();
            checkPermissionAndShowAccounts();
        });

        edtLastName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                clearFocusAndHideKeyboard();
                return true;
            }
            return false;
        });

        layoutLogin.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        butRegister.setOnClickListener(view -> {
            if (isRequestInProgress) return;   // double tap ni aaputundi

            // Validation anta RegisterValidator lo
            RegisterValidator.Result result = RegisterValidator.validate(
                    edtFirstName.getText().toString(),
                    edtLastName.getText().toString(),
                    edtEmail.getText().toString(),
                    edtNumber.getText().toString());

            if (!result.isValid()) {
                Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                focusField(result.getField());
                return;
            }

            setRegisterBusy(true);
            showLoadingDialog();   // loader start
            validationMethod(result.getFirstName(), result.getLastName(),
                    result.getMobile(), result.getEmail(), isIOS);
        });
    }

    private void clearFocusAndHideKeyboard() {
        View current = getCurrentFocus();
        if (current != null) {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(current.getWindowToken(), 0);
            current.clearFocus();
        }
        findViewById(R.id.layout_form).requestFocus();   // cursor ekkada undadu
    }

    // ---------------------------------------------------------------------
    // Button / busy state (busy = false ayite loader kuda close avtundi)
    // ---------------------------------------------------------------------

    private void setRegisterBusy(boolean busy) {
        isRequestInProgress = busy;
        butRegister.setEnabled(!busy);
        if (!busy) hideLoadingDialog();
    }

    // ---------------------------------------------------------------------
    // Error vachina field meeda focus
    // ---------------------------------------------------------------------

    private void focusField(RegisterValidator.Field field) {
        switch (field) {
            case FIRST_NAME: edtFirstName.requestFocus(); break;
            case LAST_NAME:  edtLastName.requestFocus();  break;
            case EMAIL:      edtEmail.requestFocus();     break;
            case MOBILE:     edtNumber.requestFocus();    break;
            default: break;
        }
    }

    // ---------------------------------------------------------------------
    // Permissions / SIM / email picker
    // ---------------------------------------------------------------------

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    // ---------------------------------------------------------------------
    // Loader
    // ---------------------------------------------------------------------

    private void showLoadingDialog() {
        if (isFinishing() || isDestroyed()) return;
        if (loadingDialog != null && loadingDialog.isShowing()) return;   // double dialog radu

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_loading);
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
                    new String[]{Manifest.permission.GET_ACCOUNTS}, REQUEST_GET_ACCOUNTS_PERMISSION);
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
    private void getSimNumbers() {
        showLoadingDialog();

        ArrayList<String> simNumbers = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
                != PackageManager.PERMISSION_GRANTED) {
            hideLoadingDialog();
            return;
        }

        SubscriptionManager subscriptionManager =
                (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

        if (subscriptionInfoList == null || subscriptionInfoList.isEmpty()) {
            if (simRetryCount < MAX_SIM_RETRY) {
                simRetryCount++;
                new Handler(Looper.getMainLooper()).postDelayed(this::getSimNumbers, 2000);
            } else {
                simRetryCount = 0;
                hideLoadingDialog();
                Toast.makeText(this, "Unable to fetch SIM details.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        simRetryCount = 0;
        for (SubscriptionInfo info : subscriptionInfoList) {
            String phoneNumber = info.getNumber();
            int simSlot = info.getSimSlotIndex();
            String carrierName = String.valueOf(info.getCarrierName());

            if (phoneNumber == null || phoneNumber.isEmpty()) {
                phoneNumber = "Number not available";
            }

            simNumbers.add("SIM " + (simSlot + 1) + " (" + carrierName + "): " + phoneNumber);
        }

        hideLoadingDialog();

        if (!simNumbers.isEmpty()) {
            showSimSelectionDialog(simNumbers);
        }
    }

    private void enableManualMobileEntry(String toastMessage) {
        edtNumber.setFocusableInTouchMode(true);
        edtNumber.setFocusable(true);
        edtNumber.setCursorVisible(true);
        edtNumber.requestFocus();
        edtNumber.setText("");
        edtNumber.setHint("Enter your mobile number");
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void showSimSelectionDialog(ArrayList<String> simNumbers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select SIM Number");

        boolean hasUnavailableNumber = false;
        for (String sim : simNumbers) {
            if (sim.contains("Number not available")) {
                hasUnavailableNumber = true;
                break;
            }
        }

        if (hasUnavailableNumber) {
            simNumbers.add("Enter manually");
        }

        String[] simArray = simNumbers.toArray(new String[0]);

        builder.setItems(simArray, (dialog, which) -> {
            String selected = simNumbers.get(which);

            if (selected.equals("Enter manually")) {
                enableManualMobileEntry("Please enter your number manually.");
            } else {
                String[] parts = selected.split(": ");
                String raw = (parts.length > 1) ? parts[1].trim() : selected;
                String selectedSimNumber = RegisterValidator.normalizeMobile(raw);

                if (selectedSimNumber.matches("[6-9]\\d{9}")) {
                    edtNumber.setText(selectedSimNumber);
                    edtNumber.setFocusable(false);
                    edtNumber.setCursorVisible(false);
                } else {
                    enableManualMobileEntry("Invalid SIM number detected. Please enter manually.");
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PHONE_NUMBER_PERMISSION) {
            boolean allGranted = grantResults.length > 0;
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

        if (requestCode == REQUEST_GET_ACCOUNTS_PERMISSION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showAccountPicker();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            if (credential != null) {
                edtNumber.setText(RegisterValidator.normalizeMobile(credential.getId()));
            }
        }

        if (requestCode == REQUEST_CODE_EMAIL_PICKER && resultCode == RESULT_OK && data != null) {
            String selectedEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            if (selectedEmail != null) {
                edtEmail.setText(RegisterValidator.normalizeEmail(selectedEmail));
            }
        }
    }

    private void signUpWithGoogle(boolean isSignUp) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, isSignUp ? RC_SIGN_UP_WITH_GOOGLE : RC_SIGN_IN);
    }

    // ---------------------------------------------------------------------
    // Retrofit helper
    // ---------------------------------------------------------------------

    private APiInterface createApiClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(),
                        UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(APiInterface.class);
    }

    private RequestBody text(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value == null ? "" : value);
    }

    // ---------------------------------------------------------------------
    // Register API
    // ---------------------------------------------------------------------

    private void validationMethod(String name, String lastname, String number, String email, String isIOS) {
        Log.d("API_REQUEST", "Sending -> name=" + name + ", lastname=" + lastname
                + ", mobile=" + number + ", email=" + email + ", isIOS=" + isIOS);

        APiInterface apiClient = createApiClient();

        Call<UserDataResponse> call = apiClient.postData(
                text(name), text(lastname), text(email), text(number), text(isIOS));

        call.enqueue(new Callback<UserDataResponse>() {
            @Override
            public void onResponse(Call<UserDataResponse> call, Response<UserDataResponse> response) {
                if (isFinishing() || isDestroyed()) return;

                if (!response.isSuccessful()) {
                    Log.e("RegisterActivity", "HTTP error: " + response.code());
                    Toast.makeText(RegisterActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                    setRegisterBusy(false);
                    return;
                }

                UserDataResponse body = response.body();
                if (body == null || body.getErrorCode() == null) {
                    Log.e("RegisterActivity", "UserDataResponse is null.");
                    Toast.makeText(RegisterActivity.this, "Data Error: No response received", Toast.LENGTH_LONG).show();
                    setRegisterBusy(false);
                    return;
                }

                String errorCode = body.getErrorCode();
                Log.d("RegisterActivity", "errorCode = " + errorCode);

                if ("203".equals(errorCode)) {
                    // Server cheppina asalu message ne chupinchali
                    String msg = body.getErrorMessage();
                    if (msg == null || msg.trim().isEmpty()) {
                        msg = "Email or Mobile Number already exists";
                    }
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                    setRegisterBusy(false);

                } else if ("200".equals(errorCode)) {
                    String registerId = "";
                    String otp = "";
                    List<UserDataResponse.Result> list = body.getResult();
                    if (list != null) {
                        for (UserDataResponse.Result result : list) {
                            registerId = result.getRegisterId();
                            otp = result.getOtp();
                        }
                    }
                    Log.d("RegisterActivity", "Register ID: " + registerId);

                    hideLoadingDialog();   // dialog mundu loader close
                    // button disabled ga untundi; dialog lo cancel chesthe enable avutundi
                    conformationDialog(registerId, otp);

                } else {
                    String msg = body.getErrorMessage();
                    if (msg == null || msg.trim().isEmpty()) {
                        msg = "Registration failed. Please try again.";
                    }
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                    setRegisterBusy(false);
                }
            }

            @Override
            public void onFailure(Call<UserDataResponse> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                Log.e("RegisterActivity", "Network Error: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                setRegisterBusy(false);
            }
        });
    }

    // ---------------------------------------------------------------------
    // Confirmation dialog
    // ---------------------------------------------------------------------

    private void conformationDialog(String registerId, String otp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirmation, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        CheckBox checkBox = dialogView.findViewById(R.id.simpleCheckBox);
        Button cancelButton = dialogView.findViewById(R.id.but_cancel);
        Button acceptButton = dialogView.findViewById(R.id.but_accept);
        acceptButton.setEnabled(false);

        // Okkasare create cheyali; ade dialog ni show/dismiss cheyali
        AlertDialog dialog = builder.create();

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> acceptButton.setEnabled(isChecked));

        acceptButton.setOnClickListener(v -> {
            if (!checkBox.isChecked()) {
                Toast.makeText(this, "Please agree to the privacy policy", Toast.LENGTH_SHORT).show();
                return;
            }
            acceptButton.setEnabled(false);   // malli nokkakunda
            dialog.dismiss();
            showLoadingDialog();              // OTP verify kosam loader
            otpVerfication(registerId, otp);
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            setRegisterBusy(false);
        });

        dialog.show();
    }

    // ---------------------------------------------------------------------
    // OTP verification
    // ---------------------------------------------------------------------

    private void otpVerfication(String registerId, String otp) {
        APiInterface apiClient = createApiClient();

        Call<VerifyUserDataResponse> call = apiClient.verifyData(text(registerId), text(otp));
        call.enqueue(new Callback<VerifyUserDataResponse>() {
            @Override
            public void onResponse(Call<VerifyUserDataResponse> call, Response<VerifyUserDataResponse> response) {
                if (isFinishing() || isDestroyed()) return;

                VerifyUserDataResponse body = response.body();

                if (!response.isSuccessful() || body == null || body.getErrorCode() == null) {
                    Toast.makeText(RegisterActivity.this, "Verification failed. Please try again.", Toast.LENGTH_LONG).show();
                    setRegisterBusy(false);
                    return;
                }

                if ("201".equals(body.getErrorCode())) {
                    Toast.makeText(RegisterActivity.this, "Entered OTP is invalid", Toast.LENGTH_LONG).show();
                    setRegisterBusy(false);
                } else if ("200".equals(body.getErrorCode())) {
                    hideLoadingDialog();   // success: loader close
                    Toast.makeText(RegisterActivity.this, "User Registration Completed Successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Verification failed. Please try again.", Toast.LENGTH_LONG).show();
                    setRegisterBusy(false);
                }
            }

            @Override
            public void onFailure(Call<VerifyUserDataResponse> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                Toast.makeText(RegisterActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                setRegisterBusy(false);
            }
        });
    }

    // ---------------------------------------------------------------------
    // Google sign-up (existing, currently unused)
    // ---------------------------------------------------------------------

    private void performSignUp(GoogleSignInAccount account) {
        String displayName = account.getDisplayName();
        String email = account.getEmail();
        String profilepic = String.valueOf(account.getPhotoUrl());

        sendSignUpDataToServer(displayName, email, profilepic);
    }

    private void sendSignUpDataToServer(String displayName, String email, String profilepic) {
        APiInterface apiClient = createApiClient();

        Call<SignUpWithGmail> call = apiClient.PostSignUp(text(displayName), text(email), text(profilepic));
        call.enqueue(new Callback<SignUpWithGmail>() {
            @Override
            public void onResponse(Call<SignUpWithGmail> call, Response<SignUpWithGmail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SignUpWithGmail signUpWithGmail = response.body();
                    if ("200".equals(signUpWithGmail.getErrorCode())) {
                        String displayName = "";
                        String email = "";
                        String photo = "";
                        List<SignUpWithGmail.Result> results = signUpWithGmail.getResult();
                        if (results != null) {
                            for (int i = 0; i < results.size(); i++) {
                                displayName = results.get(i).getFullName();
                                email = results.get(i).getUserEmail();
                                photo = results.get(i).getProfilePic();
                            }
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
                Log.e("RegisterActivity", "SignUp failure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(RegisterActivity.this,
                "Connection to Google Play services failed. Please try again.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();   // window leak radu
    }
}