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


        createPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePasswordChange();
            }
        });


       }




    private void handlePasswordChange() {
        mobileNumber = edtMobileNumber.getText().toString();


        if (isValidEmail(mobileNumber)) {


            validationMethod(mobileNumber);
            //Toast.makeText(RegisterActivity.this,"Data is Saved",Toast.LENGTH_LONG).show();
        }

    }

    private boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)){
            Toast.makeText(ForgotPasswordActivity.this,"Please Enter Your emailId",Toast.LENGTH_SHORT).show();
        }
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
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

}







