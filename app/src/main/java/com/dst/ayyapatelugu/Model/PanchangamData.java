package com.dst.ayyapatelugu.Model;

import java.util.List;

public class PanchangamData {
    private String vaara;
    private List<Nakshatra> nakshatra;
    private List<Tithi> tithi;
    private List<Karana> karana;
    private List<Yoga> yoga;
    private String sunrise;
    private String sunset;
    private String moonrise;
    private String moonset;
    private List<AuspiciousPeriod> auspicious_period;
    private List<InauspiciousPeriod> inauspicious_period;

    public String getVaara() {
        return vaara;
    }

    public void setVaara(String vaara) {
        this.vaara = vaara;
    }

    public List<Nakshatra> getNakshatra() {
        return nakshatra;
    }

    public void setNakshatra(List<Nakshatra> nakshatra) {
        this.nakshatra = nakshatra;
    }

    public List<Tithi> getTithi() {
        return tithi;
    }

    public void setTithi(List<Tithi> tithi) {
        this.tithi = tithi;
    }

    public List<Karana> getKarana() {
        return karana;
    }

    public void setKarana(List<Karana> karana) {
        this.karana = karana;
    }

    public List<Yoga> getYoga() {
        return yoga;
    }

    public void setYoga(List<Yoga> yoga) {
        this.yoga = yoga;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getMoonrise() {
        return moonrise;
    }

    public void setMoonrise(String moonrise) {
        this.moonrise = moonrise;
    }

    public String getMoonset() {
        return moonset;
    }

    public void setMoonset(String moonset) {
        this.moonset = moonset;
    }

    public List<AuspiciousPeriod> getAuspicious_period() {
        return auspicious_period;
    }

    public void setAuspicious_period(List<AuspiciousPeriod> auspicious_period) {
        this.auspicious_period = auspicious_period;
    }

    public List<InauspiciousPeriod> getInauspicious_period() {
        return inauspicious_period;
    }

    public void setInauspicious_period(List<InauspiciousPeriod> inauspicious_period) {
        this.inauspicious_period = inauspicious_period;
    }
}
