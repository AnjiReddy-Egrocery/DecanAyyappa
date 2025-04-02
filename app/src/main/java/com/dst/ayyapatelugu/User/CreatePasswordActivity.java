package com.dst.ayyapatelugu.User;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dst.ayyapatelugu.Model.ForgotDataResponse;
import com.dst.ayyapatelugu.Model.ResetPasswordResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreatePasswordActivity extends AppCompatActivity {

    String registerId="",otp="";
    Button butResetPassWord;

    EditText edtNewPassword, edtConfirmPassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        edtConfirmPassword=findViewById(R.id.edt_confirm_ped);
        edtNewPassword=findViewById(R.id.edt_new_pwd);
        butResetPassWord=findViewById(R.id.but_rest_pwd);

        Bundle bundle = getIntent().getExtras();
        registerId = bundle.getString("registerId");
        otp = bundle.getString("otp");
       // Log.e("Reddy","Mobile"+mobileNumber);

        butResetPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword= edtNewPassword.getText().toString();
                String confirmPassword=edtConfirmPassword.getText().toString();

                if (!doPasswordsMatch(newPassword, confirmPassword)) {
                    Toast.makeText(CreatePasswordActivity.this, "Password and ConfirmPassword do not match", Toast.LENGTH_SHORT).show();
                }

                if (isValidPassword(newPassword)
                        && doPasswordsMatch(newPassword, confirmPassword)) {

                    validationMethod(registerId,otp,newPassword);
                }
            }
        });
    }

    public void ShowHidePass(View view) {

        if(view.getId()==R.id.show_pass_btn){
            if(edtNewPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.visiablityoff);
                //Show Password
                edtNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.visiablity);
                //Hide Password
                edtNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    public void ConfirmShowHidePass(View view) {

        if(view.getId()==R.id.show_confirmpass_btn){
            if(edtConfirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.visiablityoff);
                //Show Password
                edtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.visiablity);
                //Hide Password
                edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }


    private boolean isValidPassword(String newPassword) {
        if (TextUtils.isEmpty(newPassword)){
            Toast.makeText(CreatePasswordActivity.this,"Please enter your password",Toast.LENGTH_SHORT).show();
            return false;
        }
        return newPassword.length() >= 6;
    }

    private void validationMethod(String registerId, String otp, String newPassword) {
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
        RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), registerId);
        RequestBody otpPart = RequestBody.create(MediaType.parse("text/plain"), otp);
        RequestBody PasswordPart = RequestBody.create(MediaType.parse("text/plain"), newPassword);
        Call<ResetPasswordResponse> call = apiClient.resetData(userIdPart,otpPart,PasswordPart);
        call.enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                if (response.isSuccessful()){
                    ResetPasswordResponse resetPasswordResponse=response.body();
                    if (resetPasswordResponse.getErrorCode().equals("201")){

                    }else if (resetPasswordResponse.getErrorCode().equals("200")){
                        Toast.makeText(CreatePasswordActivity.this,"User Password Recovery Done Successfully",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(CreatePasswordActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {

            }
        });
    }

    private boolean doPasswordsMatch(String newPassword, String confirmPassword) {
        return newPassword.equals(confirmPassword);
    }
}