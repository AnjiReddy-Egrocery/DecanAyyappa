package com.dst.ayyapatelugu.Model;

import java.util.List;

public class panchagamModel {

    public String status;
    public String source;
    public DataWrapper data;

    public static class DataWrapper {
        public String status;
        public PanchangData data;
    }

    public static class PanchangData {
        public String vaara;
        public List<Nakshatra> nakshatra;
        public List<Tithi> tithi;
        public List<Karana> karana;
        public List<Yoga> yoga;
        public String sunrise;
        public String sunset;
        public String moonrise;
        public String moonset;
        public List<PeriodList> auspicious_period;
        public List<PeriodList> inauspicious_period;
    }

    public static class Nakshatra {
        public int id;
        public String name;
        public Lord lord;
        public String start;
        public String end;
    }

    public static class Lord {
        public int id;
        public String name;
        public String vedic_name;
    }

    public static class Tithi {
        public int id;
        public int index;
        public String name;
        public String paksha;
        public String start;
        public String end;
    }

    public static class Karana {
        public int id;
        public int index;
        public String name;
        public String start;
        public String end;
    }

    public static class Yoga {
        public int id;
        public String name;
        public String start;
        public String end;
    }

    public static class PeriodList {
        public int id;
        public String name;
        public String type;
        public List<Period> period;
    }

    public static class Period {
        public String start;
        public String end;
    }
}