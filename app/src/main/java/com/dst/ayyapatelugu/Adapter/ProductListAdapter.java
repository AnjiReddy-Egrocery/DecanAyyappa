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

import com.dst.ayyapatelugu.Activity.AyyapaMandaliDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaMandaliListActivity;
import com.dst.ayyapatelugu.Activity.ProductDetailsActivity;
import com.dst.ayyapatelugu.Activity.ProductsListActivity;
import com.dst.ayyapatelugu.Model.BajanaManadaliListModel;
import com.dst.ayyapatelugu.Model.ProductListModel;
import com.dst.ayyapatelugu.Model.SevaListModel;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.MyviewHolder> {
    Context mContext;
    List<ProductListModel> listModels;
    private List<ProductListModel> bookListFull;

    public ProductListAdapter(ProductsListActivity productsListActivity, List<ProductListModel> productList) {
        this.mContext = productsListActivity;
        this.listModels = new ArrayList<>(productList); // Ensure listModels is modifiable
        this.bookListFull = new ArrayList<>();
        this.bookListFull.addAll(productList); // Creates a separate modifiable list
    }


    @Override
    public ProductListAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_list_adapter, parent, false);
        return new ProductListAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductListAdapter.MyviewHolder holder, int position) {
        ProductListModel modal = listModels.get(position);
        String profilePic = modal.getImage();
        String imgUrl = "https://www.ayyappatelugu.com/public/assets/productimages/" + profilePic;

        // Log the URL for debugging
        Log.d("Image URL", "Image URL: " + imgUrl);

        // Load the image using the custom loader
        ImageLoader.loadImage(mContext, imgUrl, holder.image);


        String name = modal.getName();
        String price = modal.getPrice();
        String discription = modal.getDescription();
        holder.tvtitle.setText(name);



        holder.layoutProductList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                intent.putExtra("ItemName", name);
                intent.putExtra("Price", price);
                intent.putExtra("imagePath", imgUrl);
                intent.putExtra("Discription", discription);
                mContext.startActivity(intent);

            }
        });


        // Glide.with(context).load(movieList.get(position).getImageUrl()).apply(RequestOptions.centerCropTransform()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        if (listModels != null) {
            return listModels.size();
        }
        return 0;

    }

    public Filter getFilter() {
        return productFilter;
    }

    public void updateData(List<ProductListModel> newList) {
        this.listModels = newList;
        notifyDataSetChanged(); // Notify RecyclerView to refresh the list
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView tvtitle;
        ImageView image;
        LinearLayout layoutProductList;

       // Button button;

        public MyviewHolder(View itemView) {
            super(itemView);
            tvtitle = (TextView) itemView.findViewById(R.id.txt_name);
            image = (ImageView) itemView.findViewById(R.id.img);

            layoutProductList = (LinearLayout) itemView.findViewById(R.id.layout_product_list);

            //button = (Button) itemView.findViewById(R.id.but_mostpopular);
        }
    }

    private final Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ProductListModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(bookListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ProductListModel item : bookListFull) {
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
            listModels.clear();
            listModels = new ArrayList<>((List<ProductListModel>) results.values);
            notifyDataSetChanged();
        }
    };
}