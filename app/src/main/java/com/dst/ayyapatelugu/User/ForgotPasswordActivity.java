package com.dst.ayyapatelugu.User;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dst.ayyapatelugu.Model.ForgotDataResponse;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    Button createPasswordButton;
    EditText edtMobileNumber;

    String mobileNumber;


    private static final int PHONE_NUMBER_PERMISSION_REQUEST = 121;
    private static final int REQUEST_PERMISSION = 1;
    private static final int CREDENTIAL_PICKER_REQUEST = 1001;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        edtMobileNumber = findViewById(R.id.edt_mobile_number);

        createPasswordButton = findViewById(R.id.but_create_pwd);

        edtMobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermissions();
            }

        });

        createPasswordButton.setOnClickListener(view -> handlePasswordChange());
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_NUMBERS}, REQUEST_PERMISSION);
            } else {
                getSimNumbers();
            }
        } else { // Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION);
            } else {
                getSimNumbers();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSimNumbers();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getSimNumbers() {
        ArrayList<String> simNumbers = new ArrayList<>();

        // ✅ Android 10+ (API 29): Try using TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
                    == PackageManager.PERMISSION_GRANTED) {
                String phoneNumber = telephonyManager.getLine1Number();
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    simNumbers.add("SIM 1: " + phoneNumber);
                }
            }
        }

        // ✅ Android 12 and below: Use SubscriptionManager
        if (simNumbers.isEmpty()) {
            SubscriptionManager subscriptionManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            }
            if (subscriptionManager != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                List<SubscriptionInfo> subscriptionInfoList = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                }
                if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {
                    for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                        String phoneNumber = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                            phoneNumber = subscriptionInfo.getNumber();
                        }
                        int simSlot = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                            simSlot = subscriptionInfo.getSimSlotIndex();
                        }
                        if (phoneNumber != null && !phoneNumber.isEmpty()) {
                            simNumbers.add("SIM " + (simSlot + 1) + ": " + phoneNumber);
                        }
                    }
                }
            }
        }

        // ✅ If no SIM numbers found, try Google's HintRequest API
        if (simNumbers.isEmpty()) {
            requestHint();
            return;
        }

        // ✅ If numbers found, show selection dialog
        showSimSelectionDialog(simNumbers);
    }

    private void showSimSelectionDialog(ArrayList<String> simNumbers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select SIM Number");
        String[] simArray = simNumbers.toArray(new String[0]);

        builder.setItems(simArray, (dialog, which) -> {
            String selectedSimNumber = simNumbers.get(which).split(": ")[1]; // Extract only number
            edtMobileNumber.setText(selectedSimNumber); // Set to EditText
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void requestHint() {
        CredentialsClient credentialsClient = Credentials.getClient(this);
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        try {
            startIntentSenderForResult(
                    credentialsClient.getHintPickerIntent(hintRequest).getIntentSender(),
                    CREDENTIAL_PICKER_REQUEST,
                    null, 0, 0, 0, null
            );
        } catch (Exception e) {
            Toast.makeText(this, "No SIM numbers found", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK) {
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            if (credential != null) {
                edtMobileNumber.setText(credential.getId()); // Set number from Google HintRequest API
            }
        }
    }


    private void handlePasswordChange() {
        mobileNumber = edtMobileNumber.getText().toString();


        if (isValidInputs()) {
            validationMethod(mobileNumber);
        }
    }

    private boolean isValidInputs() {
        return isValidMobileNumber(mobileNumber);
    }




    private void validationMethod(String mobileNumber) {
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
        RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), mobileNumber);


        Call<ForgotDataResponse> call = apiClient.forgotData(userIdPart);
        call.enqueue(new Callback<ForgotDataResponse>() {
            @Override
            public void onResponse(Call<ForgotDataResponse> call, Response<ForgotDataResponse> response) {
                if (response.isSuccessful()){
                    ForgotDataResponse forgotDataResponse=response.body();
                    if (forgotDataResponse.getErrorCode().equals("201")){
                        Toast.makeText(ForgotPasswordActivity.this,"Your Mobile Number is Not Registerd at. Plz Register Here..",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(ForgotPasswordActivity.this,RegisterActivity.class);
                        startActivity(intent);
                    }else if (forgotDataResponse.getErrorCode().equals("200")){
                        String registerId = "";
                        String otp= "";
                        List<ForgotDataResponse.Result> resultList=forgotDataResponse.getResult();
                        for (int i = 0; i < resultList.size(); i++) {
                            registerId = resultList.get(i).getRegisterId();
                            otp=resultList.get(i).getOtp();

                            Log.e("registerId", "registerId: " + registerId);
                            Log.e("registerId","otp"+otp);
                        }
                        Toast.makeText(ForgotPasswordActivity.this,"Reset Password Request Sent Successfully",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(ForgotPasswordActivity.this,CreatePasswordActivity.class);
                        intent.putExtra("registerId", registerId);
                        intent.putExtra("otp",otp);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<ForgotDataResponse> call, Throwable t) {

            }
        });
    }

    private boolean isValidMobileNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(ForgotPasswordActivity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return Patterns.PHONE.matcher(number).matches();
    }



}







