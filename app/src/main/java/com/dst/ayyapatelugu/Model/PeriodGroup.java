package com.dst.ayyapatelugu.Model;

import java.util.List;

public class PeriodGroup {
    private int id;
    private String name;
    private String type;
    private List<TimeRange> period;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public List<TimeRange> getPeriod() { return period; }
}
