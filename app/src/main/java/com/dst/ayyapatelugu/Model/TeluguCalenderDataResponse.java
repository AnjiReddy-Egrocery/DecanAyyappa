package com.dst.ayyapatelugu.Model;

import java.util.List;

public class TeluguCalenderDataResponse {
    private String status;
    private List<PanchangDay> data;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<PanchangDay> getData() { return data; }
    public void setData(List<PanchangDay> data) { this.data = data; }
}
