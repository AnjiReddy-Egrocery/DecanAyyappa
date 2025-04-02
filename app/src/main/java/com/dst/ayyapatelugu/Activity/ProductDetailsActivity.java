package com.dst.ayyapatelugu.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

public class ProductDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txtName, txtDiscription , txtPrice;
    ImageView imageView;

    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    TextView txtQty, txtMinus, txtPlus, txtCartBadge;
    int quantity = 0; // Default quantity



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        toolbar = findViewById(R.id.toolbar);
      /*  toolbar.setLogo(R.drawable.user_profile_background);
        toolbar.setTitle("www.ayyappatelugu.com");
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

        imageAnadanam=findViewById(R.id.layout_image_anadanam);
        imageAnadanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ProductDetailsActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductDetailsActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });


        txtName = findViewById(R.id.txt_name);
        txtDiscription = findViewById(R.id.txt_discription);
        txtPrice = findViewById(R.id.txt_price);
        imageView = findViewById(R.id.img);

        txtQty = findViewById(R.id.txt_qty);
        txtMinus = findViewById(R.id.txt_minus);
        txtPlus = findViewById(R.id.txt_plus);

        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("ItemName");
        String discription = bundle.getString("Discription");
        String prices = bundle.getString("Price");
        String imagePath = bundle.getString("imagePath");

        txtName.setText(name);
        Spanned spanned= Html.fromHtml(discription);
        String plainText=spanned.toString();
        txtDiscription.setText(plainText);
        Linkify.addLinks(txtDiscription,Linkify.WEB_URLS);
        txtPrice.setText(prices);
        Picasso.get().load(imagePath).into(imageView);

        txtPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                Log.d("CartBadge", "Plus Clicked. New Quantity: " + quantity);
                txtQty.setText(String.valueOf(quantity));
                //showCartPopup(quantity);
                updateCartBadge();
            }
        });

        txtMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    quantity--;
                    Log.d("CartBadge", "Minus Clicked. New Quantity: " + quantity);
                    txtQty.setText(String.valueOf(quantity));
                    //showCartPopup(quantity);
                    updateCartBadge();
                }
            }
        });

        invalidateOptionsMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_cart);

        if (menuItem != null) {
            View actionView = menuItem.getActionView();

            if (actionView != null) {
                txtCartBadge = actionView.findViewById(R.id.txt_cart_badge);

                if (txtCartBadge != null) {
                    Log.d("CartBadge", "txtCartBadge successfully initialized.");
                    updateCartBadge();  // Ensure badge updates immediately
                } else {
                    Log.e("CartBadge", "txtCartBadge is NULL inside actionView.");
                }

                actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));
            } else {
                Log.e("CartBadge", "actionView is NULL.");
            }
        } else {
            Log.e("CartBadge", "MenuItem for Cart is NULL.");
        }

        return super.onPrepareOptionsMenu(menu);
    }
    private void updateCartBadge() {
        Log.d("CartBadge", "updateCartBadge() called. Quantity: " + quantity);

        if (txtCartBadge != null) {
            if (quantity > 0) {
                txtCartBadge.setText(String.valueOf(quantity));
                txtCartBadge.setVisibility(View.VISIBLE);
            } else {
                txtCartBadge.setVisibility(View.GONE);
            }
        } else {
            Log.e("CartBadge", "txtCartBadge is NULL in updateCartBadge()");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            showCartPopup(quantity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCartPopup(int quantity) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_cart_popup);
        dialog.setCancelable(false);

        // Find views in dialog
        TextView txtPopupQuantity = dialog.findViewById(R.id.txt_popup_quantity);
        TextView txtPopupTotalPrice = dialog.findViewById(R.id.txt_total_price); // Added for total price
        TextView txtPrices = dialog.findViewById(R.id.txt_price);
        ImageView btnClose = dialog.findViewById(R.id.btn_close); // Fixed null error
        Button btnCart = dialog.findViewById(R.id.btn_cart);
        Button btnCheckout = dialog.findViewById(R.id.btn_checkout);

        // Example calculation logic
        String priceText = txtPrice.getText().toString().replace("₹", "").trim(); // txtPrice value fetch
        int itemPrice = Integer.parseInt(priceText); // Convert string price to integer
        int totalPrice = itemPrice * quantity;

        // Display values
        txtPopupQuantity.setText("Quantity: " + quantity);
        txtPopupTotalPrice.setText("Total: ₹" + totalPrice); // Show calculated total price
        txtPrices.setText("Total: ₹" + totalPrice);

        // Close Button Logic
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Cart Button Logic
        btnCart.setOnClickListener(v -> {
            Toast.makeText(this, "View Cart Clicked", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // Checkout Button Logic
        btnCheckout.setOnClickListener(v -> {
            Toast.makeText(this, "Proceed to Checkout", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
}