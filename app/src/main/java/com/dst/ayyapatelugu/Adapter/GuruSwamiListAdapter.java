package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dst.ayyapatelugu.Activity.GuruSwamiDetailsActivity;
import com.dst.ayyapatelugu.Activity.GuruSwamiListActivity;
import com.dst.ayyapatelugu.Model.GuruSwamiModelList;


import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.ImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GuruSwamiListAdapter extends RecyclerView.Adapter<GuruSwamiListAdapter.MyviewHolder> {

    Context mContext;
    List<GuruSwamiModelList> listModel;

    public GuruSwamiListAdapter(GuruSwamiListActivity guruSwamiListActivity, List<GuruSwamiModelList> guruswamilist) {
        this.mContext = guruSwamiListActivity;
        this.listModel = guruswamilist;

    }


    @Override
    public GuruSwamiListAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.guruswammi_list_adapter, parent, false);
        return new GuruSwamiListAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(GuruSwamiListAdapter.MyviewHolder holder, int position) {
        GuruSwamiModelList modal = listModel.get(position);
        //String profilePic = "https://www.ayyappatelugu.com/assets/user_images/" + modal.getProfilePic();

        String profilePic = modal.getProfilePic();
        String imgUrl = "https://www.ayyappatelugu.com/assets/user_images/"+profilePic ;

        String name = modal.getGuruswamiName();
     /*   String number = modal.getMobileNo();*/
        String temple = modal.getTempleName();
        String cityName = modal.getCityName();
        holder.tvtitle.setText(name);
        holder.tvaddress.setText(cityName);
      /*  holder.tvmobile.setText(number);*/

        ImageLoader.loadImage(mContext, imgUrl, holder.image);

        holder.layoutGuruSwamiAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, GuruSwamiDetailsActivity.class);
                intent.putExtra("Name", name);
               /* intent.putExtra("Number", number);*/
                intent.putExtra("Temple", temple);
                intent.putExtra("City", cityName);
                intent.putExtra("Image", imgUrl);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (listModel != null) {
            return listModel.size();
        }
        return 0;

    }


    public void updateList(List<GuruSwamiModelList> filteredList) {
        this.listModel = new ArrayList<>(filteredList);  // Ensure a fresh copy
        notifyDataSetChanged();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView tvtitle, tvaddress,tvmobile;
        ImageView image;
        LinearLayout layoutGuruSwamiAdapter;

        //Button butMostPopular;

        public MyviewHolder(View itemView) {
            super(itemView);
            tvtitle = (TextView) itemView.findViewById(R.id.txt_name);
            tvaddress = (TextView) itemView.findViewById(R.id.txt_address);
            /*tvmobile = (TextView) itemView.findViewById(R.id.txt_mobilenum);*/
            image = (ImageView) itemView.findViewById(R.id.img);
            layoutGuruSwamiAdapter = (LinearLayout) itemView.findViewById(R.id.layout_guruswami_adapter);
            //butMostPopular = (Button) itemView.findViewById(R.id.but_mostpopular);
        }

    }
}
