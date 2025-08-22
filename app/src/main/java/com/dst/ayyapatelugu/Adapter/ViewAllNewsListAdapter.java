package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dst.ayyapatelugu.Activity.ViewAllNewsDetailsActivity;
import com.dst.ayyapatelugu.Activity.ViewAllNewsListActivity;
import com.dst.ayyapatelugu.Activity.ViewAllTemplesActivity;
import com.dst.ayyapatelugu.Activity.ViewTempleListDetailsActivity;
import com.dst.ayyapatelugu.Model.BooksModelResult;
import com.dst.ayyapatelugu.Model.NewsListModel;
import com.dst.ayyapatelugu.Model.TemplesListModel;
import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewAllNewsListAdapter extends RecyclerView.Adapter<ViewAllNewsListAdapter.MyviewHolder> {
    Context mContext;
    List<NewsListModel> listModels;
    private List<NewsListModel> bookListFull; // Full list for search

    public ViewAllNewsListAdapter(ViewAllNewsListActivity viewAllNewsListActivity, List<NewsListModel> newsListModels) {
        this.mContext=viewAllNewsListActivity;
        this.listModels=newsListModels;
        this.bookListFull = new ArrayList<>(listModels); // Copy full list
    }


    @NonNull
    @Override
    public ViewAllNewsListAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.viewall_news_list_adapter, parent, false);
        return new ViewAllNewsListAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAllNewsListAdapter.MyviewHolder holder, int position) {

        NewsListModel newsListModel = listModels.get(position);
        for (NewsListModel news : listModels) {
            Log.d("News Data", "Title: " + news.getNewsTitle());
            Log.d("News Data", "Description: " + news.getDiscription());
            Log.d("News Data", "Image: " + news.getImage());
        }
        String profilepic = newsListModel.getImage();
        String imageUrl = "https://www.ayyappatelugu.com/assets/news_images/" + profilepic;
        String name = newsListModel.getNewsTitle();
        String newsDiscription = newsListModel.getDiscription();



        holder.tvNewsName.setText(name);

        // holder.tvTempleName.setText(name);
        Picasso.get().load(imageUrl).into(holder.imgNews);
        holder.layoutNewsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewAllNewsDetailsActivity.class);
                intent.putExtra("Name",name);
                intent.putExtra("imagePath", imageUrl);
                intent.putExtra("Discription",newsDiscription);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

                Log.d("ViewAllNewsDetails", "Name: " + name + ", ImagePath: " + imageUrl);
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

    public Filter getFilter() {
        return newsFilter;
    }


    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView tvNewsName;
        ImageView imgNews;
        //Button butViewAll;

        LinearLayout layoutNewsList;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            tvNewsName = itemView.findViewById(R.id.txt_name);
            imgNews = itemView.findViewById(R.id.image_news);
            layoutNewsList = itemView.findViewById(R.id.layout_news_all);
            //butViewAll = itemView.findViewById(R.id.but_mostpopular);


        }
    }

    private final Filter newsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<NewsListModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(bookListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (NewsListModel item : bookListFull) {
                    if (item.getNewsTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listModels.clear();
            listModels.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
