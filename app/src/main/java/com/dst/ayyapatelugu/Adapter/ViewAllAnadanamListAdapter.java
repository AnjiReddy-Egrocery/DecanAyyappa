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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Activity.ViewAnadanamDetailsActivity;
import com.dst.ayyapatelugu.Activity.ViewTempleListDetailsActivity;
import com.dst.ayyapatelugu.Model.AnadanamListModel;
import com.dst.ayyapatelugu.Model.TemplesListModel;
import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewAllAnadanamListAdapter extends RecyclerView.Adapter<ViewAllAnadanamListAdapter.MyviewHolder> {
    Context mContext;
    List<AnadanamListModel> listModels;
    private List<AnadanamListModel> bookListFull; // Full list for search

    public ViewAllAnadanamListAdapter(Context context, List<AnadanamListModel> templesListModels) {

        this.mContext=context;
        this.listModels = new ArrayList<>(templesListModels); // Ensure listModels is modifiable
        this.bookListFull = new ArrayList<>();
        this.bookListFull.addAll(templesListModels); // Creates a separate modifiable list

    }

    @NonNull
    @Override
    public ViewAllAnadanamListAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.viewall_anadanam_adapter, parent, false);
        return new ViewAllAnadanamListAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAllAnadanamListAdapter.MyviewHolder holder, int position) {

        AnadanamListModel templesListModel = listModels.get(position);
        String profilepic = templesListModel.getImage();
        String imageUrl = "https://www.ayyappatelugu.com/public/assets/annadhanam_images/" + profilepic;
        String id = templesListModel.getAnnadhanamId();
        String name = templesListModel.getAnnadhanamName();



        holder.tvTempleName.setText(name);

        // holder.tvTempleName.setText(name);
        Picasso.get().load(imageUrl).into(holder.imgTemple);

        holder.layoutAllTemples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewAnadanamDetailsActivity.class);
                intent.putExtra("annadhanamId",id);

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

    public Filter getFilter() {
        return ayyappatempleFilter;
    }


    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView tvTempleName;
        ImageView imgTemple;
        LinearLayout layoutAllTemples;
        //Button butViewAll;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            tvTempleName = itemView.findViewById(R.id.txt_name);
            imgTemple = itemView.findViewById(R.id.image_temple);
            layoutAllTemples=itemView.findViewById(R.id.layout_seva_all);
            // butViewAll = itemView.findViewById(R.id.but_mostpopular);


        }
    }

    private final Filter ayyappatempleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<AnadanamListModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(bookListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (AnadanamListModel item : bookListFull) {
                    if (item.getAnnadhanamName().toLowerCase().contains(filterPattern)) {
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
            listModels = new ArrayList<>((List<AnadanamListModel>) results.values);
            notifyDataSetChanged();
        }
    };
}
