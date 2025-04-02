package com.dst.ayyapatelugu.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dst.ayyapatelugu.R;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText edtOtp, edtPassword, edtConfirmPasswod;
    Button butResetPassword;
    boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edtOtp = findViewById(R.id.edt_otp);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPasswod = findViewById(R.id.edt_confirm_password);
        butResetPassword = findViewById(R.id.but_password);

        butResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAllFieldsChecked = ValidationMethod();
                if (isAllFieldsChecked) {
                    Intent i = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    private boolean ValidationMethod() {
        boolean valid = true;
        String otp = edtOtp.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPasswod.getText().toString();
        if (otp.isEmpty() || otp.length() < 6 || otp.length() > 6) {
            edtOtp.setError("Plz Enter valid Otp");
            valid = false;
        } else {
            edtOtp.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            edtPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            edtPassword.setError(null);
        }
        if (confirmPassword.isEmpty() || confirmPassword.length() < 4 || confirmPassword.length() > 10) {
            edtConfirmPasswod.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            edtConfirmPasswod.setError(null);
        }
        return valid;
    }
}