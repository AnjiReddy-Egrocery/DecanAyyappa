package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Activity.AyyapaKarmaDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaBajanaSognsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaBajanaSognsDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaKaryamListActivity;
import com.dst.ayyapatelugu.Model.BajanaSongsListModel;
import com.dst.ayyapatelugu.Model.KaryakaramamListModel;
import com.dst.ayyapatelugu.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AyyapaBajanaSongsListAdappter extends RecyclerView.Adapter<AyyapaBajanaSongsListAdappter.MyviewHolder> {
    Context mContext;
    List<BajanaSongsListModel> bajanaSongsList;

    public AyyapaBajanaSongsListAdappter(AyyappaBajanaSognsActivity ayyappaBajanaSognsActivity, List<BajanaSongsListModel> filteredList) {
        this.mContext = ayyappaBajanaSognsActivity;
        this.bajanaSongsList = filteredList;
    }


    @Override
    public AyyapaBajanaSongsListAdappter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ayyapa_songslist_adapter, parent, false);
        return new AyyapaBajanaSongsListAdappter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(AyyapaBajanaSongsListAdappter.MyviewHolder holder, int position) {
        BajanaSongsListModel modal = bajanaSongsList.get(position);

        String name = modal.getSongTitle();
        String discription = modal.getSongDescription();
        String singername = modal.getSingerName();
        holder.tvtitle.setText(name);
        // holder.tvdetails.setText(modal.getSmallDescription());


        holder.layoutPoojaList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AyyappaBajanaSognsDetailsActivity.class);
                intent.putExtra("Name", name);
                intent.putExtra("Discription", discription);
                intent.putExtra("SingerName",singername);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (bajanaSongsList != null) {
            return bajanaSongsList.size();
        }
        return 0;

    }

    public void updateList(List<BajanaSongsListModel> filteredList) {
        bajanaSongsList = filteredList;
        notifyDataSetChanged();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView tvtitle, tvdetails;


        LinearLayout layoutPoojaList;

        public MyviewHolder(View itemView) {
            super(itemView);
            tvtitle = (TextView) itemView.findViewById(R.id.txt_name);
            /*tvdetails = (TextView) itemView.findViewById(R.id.txt_details);*/

            layoutPoojaList = (LinearLayout) itemView.findViewById(R.id.layout_bajanasongs_list);
        }
    }
}
