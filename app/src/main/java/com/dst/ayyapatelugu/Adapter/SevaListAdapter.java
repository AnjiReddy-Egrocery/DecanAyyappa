package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Activity.ProductDetailsActivity;
import com.dst.ayyapatelugu.Activity.ProductsListActivity;
import com.dst.ayyapatelugu.Activity.SevaDetailsActivity;
import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.Model.ProductListModel;
import com.dst.ayyapatelugu.Model.SevaListModel;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.ImageLoader;

import java.util.List;

public class SevaListAdapter extends RecyclerView.Adapter<SevaListAdapter.MyviewHolder> {
    Context mContext;
    List<SevaListModel> listModels;

    public SevaListAdapter(HomeActivity homeActivity, List<SevaListModel> sevaList) {

        this.mContext = homeActivity;
        this.listModels = sevaList;

    }


    @Override
    public SevaListAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.seva_list_adapter, parent, false);
        return new SevaListAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(SevaListAdapter.MyviewHolder holder, int position) {
        SevaListModel modal = listModels.get(position);
        String profilePic = modal.getImage();
        String imgUrl = "https://www.ayyappatelugu.com/assets/seva_samasthalu/" + profilePic;

        // Log the URL for debugging
        Log.d("Image URL", "Image URL: " + imgUrl);

        // Load the image using the custom loader
        ImageLoader.loadImage(mContext, imgUrl, holder.image);


        String name = modal.getTitle();
        String smalldiscription= modal.getSmalldescription();
        String discription = modal.getDescription();;
        holder.tvtitle.setText(name);

        holder.layoutSevaall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SevaDetailsActivity.class);
                intent.putExtra("ItemName", name);
                intent.putExtra("SmallDiscription", smalldiscription);
                intent.putExtra("imagePath", imgUrl);
                intent.putExtra("Discription", discription);
                mContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        if (listModels != null) {
            return Math.min(listModels.size(), 4);
        }
        return 0;

    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView tvtitle;
        ImageView image;

        LinearLayout layoutSevaall;


        public MyviewHolder(View itemView) {
            super(itemView);
            tvtitle = (TextView) itemView.findViewById(R.id.txt_name);
            image = (ImageView) itemView.findViewById(R.id.image_punyam);
            layoutSevaall = itemView.findViewById(R.id.layout_seva_all);


        }
    }
}
