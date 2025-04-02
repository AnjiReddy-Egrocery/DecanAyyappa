package com.dst.ayyapatelugu.User;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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

    EditText edtEmail, edtPassword;
    Button butLogin;
    boolean isAllFieldsChecked = false;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    boolean isLoggedIn;
    //LinearLayout linearAuth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtCreateAccount = findViewById(R.id.txt_create);
        txtFpwd = findViewById(R.id.txt_fwd);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
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

        butLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginInput = edtEmail.getText().toString().trim();
                String loginPassword = edtPassword.getText().toString().trim();

                if (!loginInput.isEmpty() && isValidPassword(loginPassword)) {
                    LoginMethod(loginInput, loginPassword);
                } else {
                    Toast.makeText(LoginActivity.this, "Plz Enter Input Fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    public void ShowHidePass(View view) {

        int cursorPosition = edtPassword.getSelectionStart();

        if (edtPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            ((ImageView) (view)).setImageResource(R.drawable.visiablityoff);
            // Show Password
            edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            ((ImageView) (view)).setImageResource(R.drawable.visiablity);
            // Hide Password
            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        // Restore the cursor position
        edtPassword.setSelection(cursorPosition);
    }


    private void LoginMethod(String parentEmail, String password) {
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
        RequestBody passwordPart = RequestBody.create(MediaType.parse("text/plain"), password);

        // Call the login API
        Call<LoginDataResponse> call = apiClient.LoginData(parentEmailPart, passwordPart);

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
                        Toast.makeText(LoginActivity.this, "Incorrect Email or Password", Toast.LENGTH_LONG).show();
                    } else if (errorCode.equals("200")) {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                finish();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, "Something went Wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

}

