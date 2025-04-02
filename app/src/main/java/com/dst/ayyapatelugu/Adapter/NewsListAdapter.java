package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Activity.ViewAllNewsDetailsActivity;
import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.Model.NewsListModel;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.ImageLoader;

import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.MyviewHolder> {
    Context mContext;
    List<NewsListModel> listModels;

    public NewsListAdapter(HomeActivity homeActivity, List<NewsListModel> newsList) {
        this.mContext = homeActivity;
        this.listModels = newsList;
    }


    @Override
    public NewsListAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_list_adapter, parent, false);
        return new NewsListAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsListAdapter.MyviewHolder holder, int position) {
        NewsListModel newsListModel = listModels.get(position);


        String profilePic = newsListModel.getImage();
        String imgUrl = "https://www.ayyappatelugu.com/assets/news_images/" + profilePic;


        ImageLoader.loadImage(mContext, imgUrl, holder.image);


        String name = newsListModel.getNewsTitle();

        holder.tvtitle.setText(name);

        holder.layoutNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewAllNewsDetailsActivity.class);
                intent.putExtra("Name",name);
                intent.putExtra("imagePath", imgUrl);
                mContext.startActivity(intent);

                Log.d("ViewAllNewsDetails", "Name: " + name + ", ImagePath: " + imgUrl);
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
        TextView tvtitle;
        ImageView image;

        LinearLayout layoutNews;



        public MyviewHolder(View itemView) {
            super(itemView);
            tvtitle = itemView.findViewById(R.id.newsName);
            image = itemView.findViewById(R.id.newsImage);
            layoutNews=itemView.findViewById(R.id.layout_news);


        }
    }
}
