package com.dst.ayyapatelugu.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Activity.ViewAllNewsDetailsActivity;
import com.dst.ayyapatelugu.Activity.ViewAllNewsListActivity;
import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.Model.NewsListModel;
import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AllNewsListAdapter extends RecyclerView.Adapter<AllNewsListAdapter.MyviewHolder> {
    Context mContext;
    List<NewsListModel> listModels;
    //rivate List<NewsListModel> bookListFull; // Full list for search


    public AllNewsListAdapter(Activity applicationContext, List<NewsListModel> newsListModels) {
        this.mContext=applicationContext;
        this.listModels=newsListModels;
       // this.bookListFull = new ArrayList<>(listModels); // Copy full list
    }


    @NonNull
    @Override
    public AllNewsListAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.all_news_list_adapter, parent, false);
        return new AllNewsListAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllNewsListAdapter.MyviewHolder holder, int position) {

        NewsListModel newsListModel = listModels.get(position);



        String profilepic = newsListModel.getImage();
        String imageUrl = "https://www.ayyappatelugu.com/public/assets/news_images/" + profilepic;
        String name = newsListModel.getNewsTitle();
        String newsDiscription = newsListModel.getDiscription();

        SpannableString spannableTitle = new SpannableString("  " + name);  // Add space after bullet
        spannableTitle.setSpan(new BulletSpan(15, Color.WHITE), 0, spannableTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvNewsName.setText(spannableTitle);

        // holder.tvTempleName.setText(name);
        //Picasso.get().load(imageUrl).into(holder.imgNews);
        holder.layoutNewsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewAllNewsDetailsActivity.class);
                intent.putExtra("Name",name);
                intent.putExtra("imagePath", imageUrl);
                intent.putExtra("Discription",newsDiscription);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

                Log.d("ViewAllNewsDetails", "Clicked News Details:\n" +
                        "Name: " + name + "\n" +
                        "ImagePath: " + imageUrl + "\n" +
                        "Description: " + newsDiscription);
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
        TextView tvNewsName;
       // ImageView imgNews;
        //Button butViewAll;

        LinearLayout layoutNewsList;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            tvNewsName = itemView.findViewById(R.id.txt_name);
           // imgNews = itemView.findViewById(R.id.image_news);
            layoutNewsList = itemView.findViewById(R.id.layout_news_all);
            //butViewAll = itemView.findViewById(R.id.but_mostpopular);


        }
    }
}
