package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Model.DateTimeUtil;
import com.dst.ayyapatelugu.Model.PanchangDay;
import com.dst.ayyapatelugu.Model.PanchangamData;
import com.dst.ayyapatelugu.R;

import java.time.LocalDate;
import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.VH> {
    private final List<PanchangDay> days;
    private final Context ctx;
    private final OnDayClickListener listener;

    public interface OnDayClickListener { void onDayClick(PanchangDay day); }

    public MonthAdapter(Context ctx, List<PanchangDay> days, OnDayClickListener l) {
        this.ctx = ctx; this.days = days; this.listener = l;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_telugu_day, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH holder, int pos) {
        PanchangDay d = days.get(pos);

        // EMPTY CELL
        if (d.getDate() == null || d.getDate().isEmpty()) {
            holder.tvDate.setText("");
            holder.tvDate.setTextColor(Color.TRANSPARENT);
            holder.tvTithi.setText("");
            holder.tvNakshatra.setText("");
            holder.tvSunrise.setText("");
            return;
        }

        String fullDate = d.getDate(); // yyyy-MM-dd
        holder.tvDate.setText(fullDate.substring(8));

        // PARSE DATE
        LocalDate dateObj = null;
        try {
            dateObj = LocalDate.parse(fullDate);
        } catch (Exception ignored) {}

        // -----------------------------------
        // 🎯 TODAY DATE → GREEN
        // -----------------------------------
        LocalDate today = LocalDate.now();
        if (dateObj != null && dateObj.isEqual(today)) {
            holder.root.setBackgroundColor(Color.parseColor("#f05746")); // MAROON
            holder.tvDate.setTextColor(Color.WHITE);
            holder.tvTithi.setTextColor(Color.WHITE);
            holder.tvNakshatra.setTextColor(Color.WHITE);
            holder.tvSunrise.setTextColor(Color.WHITE);
        }
        else if (dateObj != null && dateObj.getDayOfWeek().getValue() == 7) {
            // 🎯 SUNDAY → RED
            holder.tvDate.setTextColor(Color.RED);
        }
        else {
            // 🎯 OTHER DAYS → BLACK
            holder.tvDate.setTextColor(Color.BLACK);
            holder.tvTithi.setTextColor(Color.BLACK);
            holder.tvNakshatra.setTextColor(Color.BLACK);
            holder.tvSunrise.setTextColor(Color.BLACK);
        }

        // -----------------------------------
        // PANCHANG DATA
        // -----------------------------------
        PanchangamData dd = null;
        if (d.getData() != null) {
            dd = d.getData().getData();
        }

        holder.tvTithi.setText(
                dd != null && dd.getTithi() != null && !dd.getTithi().isEmpty()
                        ? dd.getTithi().get(0).getName()
                        : ""
        );

        holder.tvNakshatra.setText(
                dd != null && dd.getNakshatra() != null && !dd.getNakshatra().isEmpty()
                        ? dd.getNakshatra().get(0).getName()
                        : ""
        );

        holder.tvSunrise.setText(
                dd != null ? DateTimeUtil.formatISOToTime(dd.getSunrise()) : ""
        );

        holder.itemView.setOnClickListener(v -> listener.onDayClick(d));
    }

    @Override public int getItemCount() { return days.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate, tvVaara, tvTithi, tvNakshatra, tvSunrise;
        View root;
        VH(View v) {
            super(v);
            root = v.findViewById(R.id.rootLayout);
            tvDate = v.findViewById(R.id.tvDate);
            tvTithi = v.findViewById(R.id.tvTithi);
            tvNakshatra = v.findViewById(R.id.tvNakshatra);
            tvSunrise = v.findViewById(R.id.tvSunrise);
        }
    }
}
