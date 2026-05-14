package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Activity.BlogDetailsActivity;
import com.dst.ayyapatelugu.Activity.BlogsActivity;
import com.dst.ayyapatelugu.Activity.PadayatraActivity;
import com.dst.ayyapatelugu.Activity.PadayatraDetailsActivity;
import com.dst.ayyapatelugu.Model.BlogDetail;
import com.dst.ayyapatelugu.Model.PadayatraBrundam;
import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AyyappaBlogListAdapter extends RecyclerView.Adapter<AyyappaBlogListAdapter.MyviewHolder> {
    Context mContext;
    List<BlogDetail> blogList;
    private List<BlogDetail> blogListFull; // Full list for search


    public AyyappaBlogListAdapter(Context context, List<BlogDetail> bloglist) {
        this.mContext = context;
        this.blogList = bloglist;
        //this.blogListFull = new ArrayList<>(blogListFull); // Copy full list

    }


    @Override
    public AyyappaBlogListAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ayyapapablog_list_adapter, parent, false);
        return new AyyappaBlogListAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(AyyappaBlogListAdapter.MyviewHolder holder, int position) {
        BlogDetail blogDetail = blogList.get(position);
        String imgUrl = "https://www.ayyappatelugu.com/public/assets/img/blog_images/" + blogDetail.getImage();
        String name = blogDetail.getTitle();

        holder.tvtitle.setText(name);
        String id = blogDetail.getBlogId();
        Picasso.get().load(imgUrl).into(holder.image);

        holder.layoutBooksList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, BlogDetailsActivity.class);
                intent.putExtra("BlogId",id);
                mContext.startActivity(intent);
            }
        });


        // Glide.with(context).load(movieList.get(position).getImageUrl()).apply(RequestOptions.centerCropTransform()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        if (blogList != null) {
            return blogList.size();
        }
        return 0;

    }

    public void setData(List<BlogDetail> blogList) {
        this.blogList = blogList;
        this.blogListFull.clear();
        this.blogListFull.addAll(blogList);
        notifyDataSetChanged();
    }


    public Filter getFilter() {
        return bookFilter;
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView tvtitle;
        ImageView image;
        LinearLayout layoutBooksList;

        // Button butViewdetails;

        public MyviewHolder(View itemView) {
            super(itemView);
            tvtitle = (TextView) itemView.findViewById(R.id.txtname);
            //tvprice = (TextView) itemView.findViewById(R.id.txt_price);
            image = (ImageView) itemView.findViewById(R.id.img_detail);
            layoutBooksList = (LinearLayout) itemView.findViewById(R.id.layout_books_list);
            // butViewdetails = (Button) itemView.findViewById(R.id.but_view_details);

        }
    }

    private final Filter bookFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BlogDetail> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(blogListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (BlogDetail item : blogListFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
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
            blogList.clear();
            blogList.addAll((List<BlogDetail>) results.values);
            notifyDataSetChanged();

            if (mContext instanceof PadayatraActivity) {
                ((PadayatraActivity) mContext).runOnUiThread(() -> {
                    RecyclerView recyclerView = ((BlogsActivity) mContext).findViewById(R.id.recycler_blog);
                    recyclerView.scrollToPosition(0);
                });
            }
        }
    };
}


