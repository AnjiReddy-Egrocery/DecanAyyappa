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

import com.dst.ayyapatelugu.Activity.AyyappaTourseActivity;
import com.dst.ayyapatelugu.Activity.AyyappaTourseDetailsACtivity;
import com.dst.ayyapatelugu.Model.YatraListModel;


import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AyyappaTourseDetailsAdapter extends RecyclerView.Adapter<AyyappaTourseDetailsAdapter.MyviewHolder> {
    Context mContext;
    List<YatraListModel> listModel;

    public AyyappaTourseDetailsAdapter(AyyappaTourseDetailsACtivity ayyappaTourseDetailsACtivity, List<YatraListModel> yatraListModels) {
        this.mContext = ayyappaTourseDetailsACtivity;
        this.listModel = yatraListModels;

    }


    @Override
    public AyyappaTourseDetailsAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tourse_details_adapter, parent, false);
        return new AyyappaTourseDetailsAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(AyyappaTourseDetailsAdapter.MyviewHolder holder, int position) {
        YatraListModel modal = listModel.get(position);
        String imgUrl = "https://www.ayyappatelugu.com/public/assets/img/tourpackage/" + modal.getImage();
        String name = modal.getNameOfPlace();
        String days = modal.getDays();
        String details = modal.getDevotees();
        String Amount = modal.getAmount();
        holder.tvtitle.setText(name);
        holder.tvaddress.setText(days);
        holder.tvdetails.setText(details);
        holder.tvamount.setText(Amount);

        Picasso.get().load(imgUrl).into(holder.image);

        holder.layoutTourseDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AyyappaTourseActivity.class);
                intent.putExtra("Name", name);
                intent.putExtra("Days", days);
                intent.putExtra("Details", details);
                intent.putExtra("Amount", Amount);
                intent.putExtra("imagePath", imgUrl);
                mContext.startActivity(intent);
            }
        });


        // Glide.with(context).load(movieList.get(position).getImageUrl()).apply(RequestOptions.centerCropTransform()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        if (listModel != null) {
            return listModel.size();
        }
        return 0;

    }

    public void updateList(List<YatraListModel> filteredList) {
        this.listModel = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView tvtitle, tvaddress, tvdetails, tvamount;
        ImageView image;
        LinearLayout layoutTourseDetails;

        //Button butDetails;

        public MyviewHolder(View itemView) {
            super(itemView);
            tvtitle = (TextView) itemView.findViewById(R.id.txt);
            tvaddress = (TextView) itemView.findViewById(R.id.txt_add);
            tvdetails = (TextView) itemView.findViewById(R.id.txt_details);
            tvamount = (TextView) itemView.findViewById(R.id.txt_amount);
            image = (ImageView) itemView.findViewById(R.id.img);
            layoutTourseDetails = (LinearLayout) itemView.findViewById(R.id.layout_tourse_details);
            //butDetails = (Button) itemView.findViewById(R.id.but_details);

        }

    }
}
