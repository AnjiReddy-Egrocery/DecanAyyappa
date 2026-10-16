package com.dst.ayyapatelugu.Model;

public class DateTimeUtil {

    // "2026-10-01T06:09:46+05:30" -> "06:09 AM"
    public static String formatISOToTime(String iso) {
        try {
            if (iso == null || iso.length() < 16) return "";
            int h = Integer.parseInt(iso.substring(11, 13));
            String m = iso.substring(14, 16);
            String ampm = h >= 12 ? "PM" : "AM";
            int h12 = h % 12;
            if (h12 == 0) h12 = 12;
            return String.format(java.util.Locale.US, "%02d:%s %s", h12, m, ampm);
        } catch (Exception e) {
            return "";
        }
    }
}