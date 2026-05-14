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

import com.dst.ayyapatelugu.Activity.AyyapaBooksListActivity;
import com.dst.ayyapatelugu.Activity.AyyappaBooksDetailsActivity;
import com.dst.ayyapatelugu.Activity.PadayatraActivity;
import com.dst.ayyapatelugu.Activity.PadayatraDetailsActivity;
import com.dst.ayyapatelugu.Model.BooksModelResult;
import com.dst.ayyapatelugu.Model.PadayatraBrundam;
import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AyyappaPadayatraListAdapter extends RecyclerView.Adapter<AyyappaPadayatraListAdapter.MyviewHolder> {
    Context mContext;
    List<PadayatraBrundam> padayatraList;
    private List<PadayatraBrundam> padayatraListFull; // Full list for search


    public AyyappaPadayatraListAdapter(PadayatraActivity padayatraActivity, List<PadayatraBrundam> padayatralist) {
        this.mContext = padayatraActivity;
        this.padayatraList = padayatralist;
        this.padayatraListFull = new ArrayList<>(padayatraList); // Copy full list

    }


    @Override
    public AyyappaPadayatraListAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ayyapapadayatra_list_adapter, parent, false);
        return new AyyappaPadayatraListAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(AyyappaPadayatraListAdapter.MyviewHolder holder, int position) {
        PadayatraBrundam padayatraBrundam = padayatraList.get(position);
        String imgUrl = "https://www.ayyappatelugu.com/public/assets/img/padayatrabrundams/" + padayatraBrundam.getImage();
        String name = padayatraBrundam.getPadayatrabrundamTelugu();

        holder.tvtitle.setText(name);
        String id = padayatraBrundam.getPadayatrabrundamId();
        Picasso.get().load(imgUrl).into(holder.image);

        holder.layoutBooksList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PadayatraDetailsActivity.class);
                intent.putExtra("padayatrabrundamId",id);
                mContext.startActivity(intent);
            }
        });


        // Glide.with(context).load(movieList.get(position).getImageUrl()).apply(RequestOptions.centerCropTransform()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        if (padayatraList != null) {
            return padayatraList.size();
        }
        return 0;

    }

    public void setData(List<PadayatraBrundam> padaytraList) {
        this.padayatraList = padaytraList;
        this.padayatraListFull.clear();
        this.padayatraListFull.addAll(padaytraList);
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
            List<PadayatraBrundam> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(padayatraListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (PadayatraBrundam item : padayatraListFull) {
                    if (item.getPadayatrabrundamTelugu().toLowerCase().contains(filterPattern)) {
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
            padayatraList.clear();
            padayatraList.addAll((List<PadayatraBrundam>) results.values);
            notifyDataSetChanged();

            if (mContext instanceof PadayatraActivity) {
                ((PadayatraActivity) mContext).runOnUiThread(() -> {
                    RecyclerView recyclerView = ((PadayatraActivity) mContext).findViewById(R.id.recycler_padayatra);
                    recyclerView.scrollToPosition(0);
                });
            }
        }
    };
}

