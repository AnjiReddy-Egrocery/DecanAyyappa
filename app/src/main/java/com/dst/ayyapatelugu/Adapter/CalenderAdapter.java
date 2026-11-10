package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Model.CalenderDataResponse;
import com.dst.ayyapatelugu.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * One card per pooja. No horizontal scrolling, works on all screen sizes.
 * Constructor accepts any Context, so CalenderActivity code needs no change:
 *   new CalenderAdapter(CalenderActivity.this, topicsList)
 */
public class CalenderAdapter extends RecyclerView.Adapter<CalenderAdapter.MyViewHolder> {

    private static final long DAY_SECONDS = 24L * 60L * 60L;

    // Past events: card slightly dim (was 0.55f -> text hard to read on the golden background)
    private static final float PAST_CARD_ALPHA = 0.9f;
    private static final float PAST_TEXT_ALPHA = 0.85f;

    private final Context mContext;
    private final List<CalenderDataResponse.Result.Poojas> poojas;

    // Server sends UTC midnight timestamps -> format in UTC + English so the date never shifts
    private final SimpleDateFormat fullFormat = utcFormat("dd MMM yyyy");
    private final SimpleDateFormat dayFormat = utcFormat("dd");
    private final SimpleDateFormat monthFormat = utcFormat("MMM");

    public CalenderAdapter(Context context, List<CalenderDataResponse.Result.Poojas> topicsList) {
        this.mContext = context;
        this.poojas = topicsList;
    }

    private static SimpleDateFormat utcFormat(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calender_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CalenderDataResponse.Result.Poojas pooja = poojas.get(position);

        long open = parseSeconds(pooja.getOpeningDate());
        long close = parseSeconds(pooja.getClosingDate());

        // ---- Date badge (left) ----
        if (open > 0) {
            Date openDate = new Date(open * 1000L);
            holder.txtDay.setText(dayFormat.format(openDate));
            holder.txtMonth.setText(monthFormat.format(openDate).toUpperCase(Locale.ENGLISH));
        } else {
            holder.txtDay.setText("--");
            holder.txtMonth.setText(safe(pooja.getMonthName()));
        }

        // ---- Pooja name ----
        holder.txtName.setText(safe(pooja.getPoojaName()));

        // ---- Date range ----
        String dateText;
        if (open <= 0) {
            dateText = "--";
        } else if (close <= 0 || close == open) {
            dateText = fullFormat.format(new Date(open * 1000L)); // single-day event
        } else {
            dateText = fullFormat.format(new Date(open * 1000L))
                    + "  \u2192  "
                    + fullFormat.format(new Date(close * 1000L));
        }
        holder.txtDates.setText(dateText);

        // ---- Past events: only a light dim so text stays readable ----
        // Always set both branches (RecyclerView reuses views)
        long endSeconds = (close > 0 ? close : open) + DAY_SECONDS;
        boolean isPast = open > 0 && System.currentTimeMillis() >= endSeconds * 1000L;

        holder.itemView.setAlpha(isPast ? PAST_CARD_ALPHA : 1f);
        holder.txtName.setAlpha(isPast ? PAST_TEXT_ALPHA : 1f);
        holder.txtDates.setAlpha(isPast ? PAST_TEXT_ALPHA : 1f);
    }

    @Override
    public int getItemCount() {
        return poojas == null ? 0 : poojas.size();
    }

    private long parseSeconds(String value) {
        if (value == null) return 0;
        String v = value.trim();
        if (v.isEmpty()) return 0;
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String safe(String text) {
        return text == null ? "" : text;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView txtDay, txtMonth, txtName, txtDates;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDay = itemView.findViewById(R.id.txt_day);
            txtMonth = itemView.findViewById(R.id.txt_month);
            txtName = itemView.findViewById(R.id.txt_pooja_name);
            txtDates = itemView.findViewById(R.id.txt_dates);
        }
    }
}