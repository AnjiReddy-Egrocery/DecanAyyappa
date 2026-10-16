package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Model.PanchangDay;
import com.dst.ayyapatelugu.Model.PanchangamData;
import com.dst.ayyapatelugu.R;

import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.VH> {

    public interface OnDayClick {
        void onClick(PanchangDay day);
    }

    private final Context context;
    private final List<PanchangDay> days;
    private final OnDayClick listener;

    public MonthAdapter(Context context, List<PanchangDay> days, OnDayClick listener) {
        this.context = context;
        this.days = days;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_telugu_day, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PanchangDay day = days.get(position);

        // Empty offset cell
        if (day == null || day.getDate() == null) {
            h.itemView.setVisibility(View.INVISIBLE);
            h.itemView.setOnClickListener(null);
            return;
        }
        h.itemView.setVisibility(View.VISIBLE);

        // Date number
        String dayNum = String.valueOf(Integer.parseInt(day.getDate().substring(8, 10)));
        h.tvDate.setText(dayNum);

        PanchangamData d = (day.getData() != null) ? day.getData().getData() : null;

        String tithi = "", nakshatra = "";
        if (d != null) {
            if (d.getTithi() != null && !d.getTithi().isEmpty())
                tithi = d.getTithi().get(0).getName();
            if (d.getNakshatra() != null && !d.getNakshatra().isEmpty())
                nakshatra = d.getNakshatra().get(0).getName();
        }
        h.tvTithi.setText(tithi);
        h.tvNakshatra.setText(nakshatra);

        // Sunday red
        boolean sunday = d != null && d.getVaara() != null && d.getVaara().startsWith("ఆది");
        h.tvDate.setTextColor(sunday ? Color.parseColor("#D32F2F") : Color.parseColor("#000000"));

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(day);
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate, tvTithi, tvNakshatra;

        VH(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTithi = itemView.findViewById(R.id.tvTithi);
            tvNakshatra = itemView.findViewById(R.id.tvNakshatra);
        }
    }
}