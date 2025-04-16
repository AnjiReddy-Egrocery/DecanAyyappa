package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dst.ayyapatelugu.Activity.AyyapaMandaliDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaMandaliListActivity;
import com.dst.ayyapatelugu.Model.BajanaManadaliListModel;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AyyapamandaliAdapter extends RecyclerView.Adapter<AyyapamandaliAdapter.MyviewHolder> {
    Context mContext;
    List<BajanaManadaliListModel> listModels;


    public AyyapamandaliAdapter(AyyappaMandaliListActivity ayyappaMandaliListActivity, List<BajanaManadaliListModel> list) {
        this.mContext = ayyappaMandaliListActivity;
        this.listModels = list;

    }
    @Override
    public AyyapamandaliAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ayyapamandali_list_adapter, parent, false);
        return new AyyapamandaliAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(AyyapamandaliAdapter.MyviewHolder holder, int position) {
        BajanaManadaliListModel modal = listModels.get(position);
        String profilePic = modal.getProfilePic();
        String imgUrl = "https://www.ayyappatelugu.com/assets/user_images/"+profilePic ;

        // Log the URL for debugging
        Log.d("Image URL", "Image URL: " + imgUrl);

        // Load the image using the custom loader
        ImageLoader.loadImage(mContext, imgUrl, holder.image);



        String name = modal.getNameOfGuru();
        String GuruNme = modal.getNameOfGuru();
        String City = modal.getBajanamandaliCity();
        String Number = modal.getBajanamandaliMobile();
        String Email = modal.getBajanamandaliEmail();
        String discription = modal.getBajanamandaliDescription();
        holder.tvtitle.setText(name);
        holder.tvadd.setText(modal.getBajanamandaliLocation());
       /* holder.tvMobile.setText(Number);*/


        holder.layoutMandaliList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, AyyapaMandaliDetailsActivity.class);
                intent.putExtra("ItemName", name);
                intent.putExtra("ItemGuruName", GuruNme);
                intent.putExtra("ItemCity", City);
                intent.putExtra("ItemNumber", Number);
                intent.putExtra("ItemEmail", Email);
                intent.putExtra("imagePath", imgUrl);
                intent.putExtra("Discription", discription);
                mContext.startActivity(intent);

            }
        });


        // Glide.with(context).load(movieList.get(position).getImageUrl()).apply(RequestOptions.centerCropTransform()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        if (listModels != null) {
            return listModels.size();
        }
        return 0;

    }



    public void updateList(List<BajanaManadaliListModel> filteredList) {
        this.listModels = new ArrayList<>(filteredList);  // Ensure a fresh copy
        notifyDataSetChanged();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView tvtitle, tvadd,tvMobile;
        ImageView image;
        LinearLayout layoutMandaliList;

        //Button button;

        public MyviewHolder(View itemView) {
            super(itemView);
            tvtitle = (TextView) itemView.findViewById(R.id.txt_name);
            tvadd = (TextView) itemView.findViewById(R.id.txt_address);
           /* tvMobile = (TextView) itemView.findViewById(R.id.txt_mobile);*/
            image = (ImageView) itemView.findViewById(R.id.img);
            layoutMandaliList=(LinearLayout) itemView.findViewById(R.id.layout_mandali_list);

           // button = (Button) itemView.findViewById(R.id.but_mostpopular);
        }
    }
}
