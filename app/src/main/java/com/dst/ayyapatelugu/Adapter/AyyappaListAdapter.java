package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Activity.ViewTempleListDetailsActivity;
import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.Model.AyyaTempleListModel;
import com.dst.ayyapatelugu.Model.TemplesListModel;
import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AyyappaListAdapter extends RecyclerView.Adapter<AyyappaListAdapter.MyviewHolder> {
    Context mContext;
    List<AyyaTempleListModel> listModels;

    public AyyappaListAdapter(HomeActivity homeActivity, List<AyyaTempleListModel> templesListModels) {
        this.mContext = homeActivity;
        this.listModels = templesListModels;
    }


    @NonNull
    @Override
    public AyyappaListAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ayyappatemple_list_adapter, parent, false);
        return new AyyappaListAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AyyappaListAdapter.MyviewHolder holder, int position) {

        AyyaTempleListModel templesListModel = listModels.get(position);
        String profilepic = templesListModel.getImage();
        String imageUrl = "https://www.ayyappatelugu.com/assets/temple_images/" + profilepic;
        String name = templesListModel.getTempleName();

        if (name.length() > 40) { // Arbitrary length, adjust based on design
            holder.tvTempleName.setTextSize(14); // Reduce font size for long names
            holder.tvTempleName.setText(name.substring(0, 40) + "..."); // Add ellipsis
        } else {
            holder.tvTempleName.setTextSize(14); // Regular font size
            holder.tvTempleName.setText(name);
        }

        String tName=templesListModel.getTempleNameTelugu();
        String open=templesListModel.getOpeningTime();
        String close=templesListModel.getClosingTime();
        String location= templesListModel.getLocation();

        // holder.tvTempleName.setText(name);
        Picasso.get().load(imageUrl).into(holder.imgTemple);
        holder.layoutTemples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewTempleListDetailsActivity.class);
                intent.putExtra("Name",name);
                intent.putExtra("TName",tName);
                intent.putExtra("Open",open);
                intent.putExtra("Close",close);
                intent.putExtra("Location",location);
                intent.putExtra("imagePath", imageUrl);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listModels != null) {
            return listModels.size();
        }
        return 0;

    }


    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView tvTempleName;
        ImageView imgTemple;

        LinearLayout layoutTemples;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            tvTempleName = itemView.findViewById(R.id.templeName);
            imgTemple = itemView.findViewById(R.id.templeImage);
            layoutTemples = itemView.findViewById(R.id.layout_temples);


        }
    }
}