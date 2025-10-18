package com.dst.ayyapatelugu.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Activity.AyyapaPetamDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaPetamListActivity;
import com.dst.ayyapatelugu.Model.decoratormodelResult;

import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AyyappaPetamListAdapteer extends RecyclerView.Adapter<AyyappaPetamListAdapteer.MyviewHolder> {
    Context mContext;
    List<decoratormodelResult> decoratorList;


    public AyyappaPetamListAdapteer(AyyappaPetamListActivity ayyappaPetamListActivity, List<decoratormodelResult> decoratorList) {
        this.mContext = ayyappaPetamListActivity;
        this.decoratorList = decoratorList;

    }

    public void setdecoratorList(ArrayList<decoratormodelResult> decoratorList) {
        this.decoratorList = decoratorList;
        notifyDataSetChanged();
    }

    @Override
    public AyyappaPetamListAdapteer.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ayyapapetam_list_adapter, parent, false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(AyyappaPetamListAdapteer.MyviewHolder holder, int position) {
        decoratormodelResult modal = decoratorList.get(position);
        String imgUrl = "https://www.ayyappatelugu.com/public/assets/img/decorators/" + modal.getProfilePic();
        String dname = modal.getDecoratorName();
        String fname = modal.getFullName();
        String city = modal.getCityName();
        String specilazation = modal.getSpecialization();
        String village = modal.getVillageName();
        String number = modal.getMobileNumber();
        String email = modal.getEmailId();
        String discription = modal.getDecoratorDescription();
        holder.tvtitle.setText(dname);
      /*  holder.tvMobile.setText(number);*/
        holder.tvLocation.setText(city);

        Picasso.get().load(imgUrl).into(holder.image);

        holder.layoutPetamList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AyyapaPetamDetailsActivity.class);
                intent.putExtra("Dname", dname);
                intent.putExtra("Fname", fname);
                intent.putExtra("City", city);
                intent.putExtra("Specilization", specilazation);
                intent.putExtra("Village", village);
                intent.putExtra("Number", number);
                intent.putExtra("Email", email);
                intent.putExtra("Discription", discription);
                intent.putExtra("ImagePath", imgUrl);
                mContext.startActivity(intent);
            }
        });


        // Glide.with(context).load(movieList.get(position).getImageUrl()).apply(RequestOptions.centerCropTransform()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        if (decoratorList != null) {
            return decoratorList.size();
        }
        return 0;

    }

    public void updateList(List<decoratormodelResult> filteredList) {
        this.decoratorList = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView tvtitle,tvLocation,tvMobile;
        ImageView image;
        LinearLayout layoutPetamList;

        //Button button;


        public MyviewHolder(View itemView) {
            super(itemView);
            tvtitle = (TextView) itemView.findViewById(R.id.txt_name);
            tvLocation = (TextView) itemView.findViewById(R.id.txt_location);
      /*      tvMobile = (TextView) itemView.findViewById(R.id.txt_mobile);*/
            image = (ImageView) itemView.findViewById(R.id.img);
            layoutPetamList = (LinearLayout) itemView.findViewById(R.id.layout_petam_list);
            //button = (Button) itemView.findViewById(R.id.but_details);
        }
    }
}
