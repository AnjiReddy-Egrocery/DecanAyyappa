package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Activity.AyyapaBooksListActivity;
import com.dst.ayyapatelugu.Activity.AyyappaBooksDetailsActivity;
import com.dst.ayyapatelugu.Model.BooksModelResult;

import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AyyappaBooksListAdapter extends RecyclerView.Adapter<AyyappaBooksListAdapter.MyviewHolder> {
    Context mContext;
    List<BooksModelResult> bookList;
    private List<BooksModelResult> bookListFull; // Full list for search


    public AyyappaBooksListAdapter(AyyapaBooksListActivity ayyapaBooksListActivity, List<BooksModelResult> books) {
        this.mContext = ayyapaBooksListActivity;
        this.bookList = books;
        this.bookListFull = new ArrayList<>(bookList); // Copy full list

    }


    @Override
    public AyyappaBooksListAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ayyapabooks_list_adapter, parent, false);
        return new AyyappaBooksListAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(AyyappaBooksListAdapter.MyviewHolder holder, int position) {
        BooksModelResult modal = bookList.get(position);
        String imgUrl = "https://www.ayyappatelugu.com/public/assets/bookimage/" + modal.getImage();
        String name = modal.getName();
        String price = modal.getPrice();
        String author = modal.getAuthor();
        String pages = modal.getPages();
        String published = modal.getPublishedOn();
        holder.tvtitle.setText(name);
        //holder.tvprice.setText(price);
        Picasso.get().load(imgUrl).into(holder.image);

        holder.layoutBooksList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AyyappaBooksDetailsActivity.class);
                intent.putExtra("Name", name);
                intent.putExtra("Price", price);
                intent.putExtra("Author", author);
                intent.putExtra("Pages", pages);
                intent.putExtra("Published", published);
                intent.putExtra("ImageAuth", imgUrl);
                mContext.startActivity(intent);
            }
        });


        // Glide.with(context).load(movieList.get(position).getImageUrl()).apply(RequestOptions.centerCropTransform()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        if (bookList != null) {
            return bookList.size();
        }
        return 0;

    }

    public void setData(List<BooksModelResult> bookList) {
        this.bookList = bookList;
        this.bookListFull.clear();
        this.bookListFull.addAll(bookList);
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
            List<BooksModelResult> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(bookListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (BooksModelResult item : bookListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
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
            bookList.clear();
            bookList.addAll((List<BooksModelResult>) results.values);
            notifyDataSetChanged();

            if (mContext instanceof AyyapaBooksListActivity) {
                ((AyyapaBooksListActivity) mContext).runOnUiThread(() -> {
                    RecyclerView recyclerView = ((AyyapaBooksListActivity) mContext).findViewById(R.id.recycler_books);
                    recyclerView.scrollToPosition(0);
                });
            }
        }
    };
}
