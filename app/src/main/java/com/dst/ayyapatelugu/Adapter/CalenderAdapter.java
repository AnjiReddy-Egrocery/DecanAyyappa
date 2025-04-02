package com.dst.ayyapatelugu.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Activity.CalenderActivity;
import com.dst.ayyapatelugu.Model.CalenderDataResponse;
import com.dst.ayyapatelugu.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalenderAdapter extends RecyclerView.Adapter<CalenderAdapter.MyViewHolder> {
    Context mContext;
    List<CalenderDataResponse.Result.Poojas> poojas;
    public CalenderAdapter(CalenderActivity calenderActivity, List<CalenderDataResponse.Result.Poojas> topicsList) {
        this.mContext=calenderActivity;
        this.poojas=topicsList;
    }

    @NonNull
    @Override
    public CalenderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calender_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalenderAdapter.MyViewHolder holder, int position) {
        holder.bindData(poojas);
    }

    @Override
    public int getItemCount() {
        return 1; // Table should be displayed only once
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TableLayout tableLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tableLayout = itemView.findViewById(R.id.table_layout);
        }
      @SuppressLint("ResourceAsColor")
        public void bindData(List<CalenderDataResponse.Result.Poojas> poojaList) {
            tableLayout.removeAllViews(); // Clear previous rows

            // *Add Header Row*
            TableRow headerRow = new TableRow(mContext);
            headerRow.setBackgroundResource(R.drawable.header_bg); // Header background
            headerRow.setPadding(8, 8, 8, 8);

            headerRow.addView(createHeaderTextView("Month", 180));
            headerRow.addView(createHeaderTextView("Opening Date", 150));
            headerRow.addView(createHeaderTextView("Closing Date", 150));
            headerRow.addView(createHeaderTextView("Pooja Name", 300));

            tableLayout.addView(headerRow);

            // *Add Data Rows*
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            for (int i = 0; i < poojaList.size(); i++) {
                CalenderDataResponse.Result.Poojas pooja = poojaList.get(i);
                String formattedOpenDate = "--", formattedCloseDate = "--";

                if (pooja.getOpeningDate() != null && !pooja.getOpeningDate().isEmpty()) {
                    formattedOpenDate = dateFormat.format(new Date(Long.parseLong(pooja.getOpeningDate()) * 1000));
                }
                if (pooja.getClosingDate() != null && !pooja.getClosingDate().isEmpty()) {
                    formattedCloseDate = dateFormat.format(new Date(Long.parseLong(pooja.getClosingDate()) * 1000));
                }

                TableRow dataRow = new TableRow(mContext);
                dataRow.setPadding(8, 8, 8, 8);

                // *Make Every Alternate Row Light Transparent*
                if (i % 2 == 0) {
                    dataRow.setBackgroundColor(mContext.getResources().getColor(R.color.row_light_transparent)); // Light transparent color
                } else {
                    dataRow.setBackgroundColor(android.R.color.transparent); // No color
                }

                dataRow.addView(createMultiLineTextView(pooja.getMonthName(), 180));
                dataRow.addView(createTextView(formattedOpenDate, 150));
                dataRow.addView(createTextView(formattedCloseDate, 150));
                dataRow.addView(createMultiLineTextView(pooja.getPoojaName(), 300));

                tableLayout.addView(dataRow);
            }
        }



        private TextView createTextView(String text, int width) {
            TextView textView = new TextView(mContext);
            textView.setText(text);
            textView.setTextColor(mContext.getResources().getColor(R.color.white)); // White text color
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(8, 8, 8, 8);
            textView.setMaxLines(1);
            textView.setEllipsize(null);
            textView.setWidth(width);
            return textView;
        }

        private TextView createMultiLineTextView(String text, int width) {
            TextView textView = new TextView(mContext);
            textView.setText(text);
            textView.setTextColor(mContext.getResources().getColor(R.color.white)); // White text color
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(8, 8, 8, 8);
            textView.setMaxLines(3); // Allow maximum 3 lines
            textView.setEllipsize(null);
            textView.setWidth(width);
            return textView;
        }

        private TextView createHeaderTextView(String text, int width) {
            TextView textView = new TextView(mContext);
            textView.setText(text);
            textView.setTextColor(mContext.getResources().getColor(R.color.white)); // White text color
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(8, 8, 8, 8);
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
            textView.setBackgroundResource(R.drawable.header_bg); // Apply same header background
            textView.setWidth(width);
            return textView;
        }
    }
}