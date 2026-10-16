package com.dst.ayyapatelugu.Model;

import java.util.List;

public class PanchangamData {
    private String vaara;
    private List<NameTime> nakshatra;
    private List<NameTime> tithi;
    private List<NameTime> karana;
    private List<NameTime> yoga;
    private String sunrise;
    private String sunset;
    private String moonrise;
    private String moonset;
    private List<PeriodGroup> auspicious_period;
    private List<PeriodGroup> inauspicious_period;

    public String getVaara() { return vaara; }
    public List<NameTime> getNakshatra() { return nakshatra; }
    public List<NameTime> getTithi() { return tithi; }
    public List<NameTime> getKarana() { return karana; }
    public List<NameTime> getYoga() { return yoga; }
    public String getSunrise() { return sunrise; }
    public String getSunset() { return sunset; }
    public String getMoonrise() { return moonrise; }
    public String getMoonset() { return moonset; }
    public List<PeriodGroup> getAuspicious_period() { return auspicious_period; }
    public List<PeriodGroup> getInauspicious_period() { return inauspicious_period; }
}