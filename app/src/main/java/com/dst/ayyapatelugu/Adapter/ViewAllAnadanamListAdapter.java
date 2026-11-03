package com.dst.ayyapatelugu.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
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
import com.dst.ayyapatelugu.Model.AnadanamListModel;
import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewAllAnadanamListAdapter extends RecyclerView.Adapter<ViewAllAnadanamListAdapter.MyviewHolder> {
    Context mContext;
    List<AnadanamListModel> listModels;
    private List<AnadanamListModel> bookListFull; // Full list for search

    // Current user location (null aite distance chupinchadu)
    private Double userLat = null;
    private Double userLng = null;

    public ViewAllAnadanamListAdapter(Context context, List<AnadanamListModel> templesListModels) {

        this.mContext = context;
        this.listModels = new ArrayList<>(templesListModels);
        this.bookListFull = new ArrayList<>();
        this.bookListFull.addAll(templesListModels);
    }

    // Fragment nundi current location set cheyadaniki
    public void setUserLocation(double lat, double lng) {
        this.userLat = lat;
        this.userLng = lng;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.viewall_anadanam_adapter, parent, false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {

        AnadanamListModel templesListModel = listModels.get(position);
        String profilepic = templesListModel.getImage();
        String imageUrl = "https://www.ayyappatelugu.com/public/assets/annadhanam_images/" + profilepic;
        String id = templesListModel.getAnnadhanamId();
        String name = templesListModel.getAnnadhanamNameTelugu() + "," + templesListModel.getLocationenglish();

        holder.tvTempleName.setText(name);

        Picasso.get().load(imageUrl).into(holder.imgTemple);

        holder.layoutAllTemples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewAnadanamDetailsActivity.class);
                intent.putExtra("annadhanamId", id);

                mContext.startActivity(intent);
            }
        });

        // ---------- Latitude / Longitude ----------
        Double destLat = parseCoordinate(templesListModel.getLatitude());
        Double destLng = parseCoordinate(templesListModel.getLongitude());

        if (destLat == null || destLng == null) {
            // Coordinates ledu -> distance, direction rendu hide
            holder.tvDistance.setVisibility(View.GONE);
            holder.btnDirection.setVisibility(View.GONE);
            return;
        }

        holder.btnDirection.setVisibility(View.VISIBLE);

        // ---------- Distance ----------
        if (userLat != null && userLng != null) {
            float[] result = new float[1];
            Location.distanceBetween(userLat, userLng, destLat, destLng, result);
            holder.tvDistance.setText(formatDistance(result[0]));
            holder.tvDistance.setVisibility(View.VISIBLE);
        } else {
            holder.tvDistance.setVisibility(View.GONE);
        }

        // ---------- Get Direction -> Navigation ----------
        final double lat = destLat;
        final double lng = destLng;
        holder.btnDirection.setOnClickListener(v -> startNavigation(lat, lng));
    }

    private Double parseCoordinate(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String formatDistance(float meters) {
        if (meters < 1000) {
            return String.format(Locale.US, "%d m", Math.round(meters));
        }
        return String.format(Locale.US, "%.1f km", meters / 1000f);
    }

    private void startNavigation(double lat, double lng) {
        try {
            // Google Maps lo direct ga turn-by-turn navigation
            Uri uri = Uri.parse("google.navigation:q=" + lat + "," + lng);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Google Maps app lekapote browser / vere map app lo open
            Uri webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + lat + "," + lng);
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, webUri));
        }
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
        TextView tvDistance;
        ImageView imgTemple;
        LinearLayout layoutAllTemples;
        LinearLayout btnDirection;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            tvTempleName = itemView.findViewById(R.id.txt_name);
            tvDistance = itemView.findViewById(R.id.txt_distance);
            imgTemple = itemView.findViewById(R.id.image_temple);
            layoutAllTemples = itemView.findViewById(R.id.layout_seva_all);
            btnDirection = itemView.findViewById(R.id.start_navigation_button);
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
                    String itemName = item.getAnnadhanamName();
                    if (itemName != null && itemName.toLowerCase().contains(filterPattern)) {
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
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listModels = new ArrayList<>((List<AnadanamListModel>) results.values);
            notifyDataSetChanged();
        }
    };
}