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

import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Activity.ProductDetailsActivity;
import com.dst.ayyapatelugu.Activity.SevaDetailsActivity;
import com.dst.ayyapatelugu.Activity.ViewAllDetailsActivity;
import com.dst.ayyapatelugu.Model.BooksModelResult;
import com.dst.ayyapatelugu.Model.SevaListModel;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ViewAllListAdapter extends RecyclerView.Adapter<ViewAllListAdapter.MyviewHolder> {
    Context mContext;
    List<SevaListModel> listModels;
    private List<SevaListModel> bookListFull;
    public ViewAllListAdapter(ViewAllDetailsActivity viewAllDetailsActivity, List<SevaListModel> sevaList) {

        this.mContext=viewAllDetailsActivity;
        this.listModels = new ArrayList<>(sevaList); // Ensure listModels is modifiable
        this.bookListFull = new ArrayList<>();
        this.bookListFull.addAll(sevaList); // Creates a separate modifiable list

    }

    @Override
    public ViewAllListAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.viewall_list_adapter, parent, false);
        return new ViewAllListAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewAllListAdapter.MyviewHolder holder, int position) {
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

        holder.layoutSevaAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
            return listModels.size();
        }
        return 0;

    }

    public Filter getFilter() {
        return sevaFilter;
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView tvtitle;
        ImageView image;
        LinearLayout layoutSevaAll;

       // Button button;


        public MyviewHolder(View itemView) {
            super(itemView);
            tvtitle = (TextView) itemView.findViewById(R.id.txt_name);
            image = (ImageView) itemView.findViewById(R.id.image_punyam);
            layoutSevaAll = (LinearLayout) itemView.findViewById(R.id.layout_seva_all);

            //button = (Button) itemView.findViewById(R.id.but_mostpopular);


        }
    }

    private final Filter sevaFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SevaListModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(bookListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (SevaListModel item : bookListFull) {
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
              listModels.clear();
            listModels = new ArrayList<>((List<SevaListModel>) results.values);
            notifyDataSetChanged();
        }
    };
}
