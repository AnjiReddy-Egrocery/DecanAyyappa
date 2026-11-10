package com.dst.ayyapatelugu.Activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dst.ayyapatelugu.R;

public class SendbajanaDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText edtBajanaMandaliName, edtContactPersonName, edtMobileNumber;
    Button butSubmit;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendbajana_details);

        toolbar = findViewById(R.id.toolbar);
        edtBajanaMandaliName = findViewById(R.id.edt_name_bajanamandali);
        edtContactPersonName = findViewById(R.id.edt_contact_person);
        edtMobileNumber = findViewById(R.id.edt_number);
        butSubmit = findViewById(R.id.but_submit);

        setSupportActionBar(toolbar);
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

        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    String name = edtBajanaMandaliName.getText().toString().trim();
                    String temple = edtContactPersonName.getText().toString().trim();
                    String mobile = edtMobileNumber.getText().toString().trim();

                    // TODO: ikkada API call cheyandi (name, temple, mobile pampandi)
                    Toast.makeText(SendbajanaDetailsActivity.this,
                            "Details submitted successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validate() {
        String name = edtBajanaMandaliName.getText().toString().trim();
        String temple = edtContactPersonName.getText().toString().trim();
        String mobile = edtMobileNumber.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            edtBajanaMandaliName.setError("Please enter BajanaMandali name");
            edtBajanaMandaliName.requestFocus();
            return false;
        }
        if (name.length() < 3) {
            edtBajanaMandaliName.setError("Name must be at least 3 characters");
            edtBajanaMandaliName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(temple)) {
            edtContactPersonName.setError("Please enter contact person name");
            edtContactPersonName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(mobile)) {
            edtMobileNumber.setError("Please enter mobile number");
            edtMobileNumber.requestFocus();
            return false;
        }
        // Indian mobile: 10 digits, 6-9 tho start avali
        if (!mobile.matches("[6-9][0-9]{9}")) {
            edtMobileNumber.setError("Enter a valid 10 digit mobile number");
            edtMobileNumber.requestFocus();
            return false;
        }
        return true;
    }

}