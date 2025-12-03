package com.dst.ayyapatelugu.Model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    // Converts ISO DateTime → HH:mm (24-hour)
    public static String formatISOToTime(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";

        try {
            // Example: "2025-02-11T06:22:00+05:30" or "06:22"
            if (isoDate.contains("T")) {
                SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date d = iso.parse(isoDate.substring(0, 19));
                SimpleDateFormat out = new SimpleDateFormat("HH:mm");
                return out.format(d);
            } else {
                // if API gives direct time like "06:22"
                return isoDate;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return isoDate; // fallback
        }
    }
}

