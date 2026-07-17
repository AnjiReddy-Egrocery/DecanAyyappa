package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Model.MapDataResponse;
import com.dst.ayyapatelugu.R;

import java.util.ArrayList;

public class NearbyAnnadanamAdapter extends RecyclerView.Adapter<NearbyAnnadanamAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MapDataResponse.Result> list;
    private OnTempleClickListener listener;

    public interface OnTempleClickListener {
        void onTempleClick(MapDataResponse.Result item);
    }

    public NearbyAnnadanamAdapter(Context context,
                                  ArrayList<MapDataResponse.Result> list,
                                  OnTempleClickListener listener) {

        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_nearby_annadanam, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MapDataResponse.Result item = list.get(position);

        holder.txtTempleName.setText(item.getAnnadhanamNameTelugu());

        holder.txtLocation.setText(item.getLocation());

        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {
                listener.onTempleClick(item);
            }

        });
    }

    @Override
    public int getItemCount() {

        return list == null ? 0 : list.size();

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTempleName;
        TextView txtLocation;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTempleName = itemView.findViewById(R.id.txtTempleName);
            txtLocation = itemView.findViewById(R.id.txtLocation);
        }
    }
}
