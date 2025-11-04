package com.dst.ayyapatelugu.Model;

import java.util.List;

public class InauspiciousPeriod {
    private int id;
    private String name;
    private String type;
    private List<Period> period;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Period> getPeriod() {
        return period;
    }

    public void setPeriod(List<Period> period) {
        this.period = period;
    }
}
