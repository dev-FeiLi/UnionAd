package io.union.wap.model;

public class AffDateTypeData {

    private String date;
    private Long uid;
    private Double cpm = 0.0;
    private Double cpv = 0.0;
    private Double cps = 0.0;
    private Double cpa = 0.0;
    private Double cpc = 0.0;
    private Double total = 0.0;

    public AffDateTypeData() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Double getCpm() {
        return cpm;
    }

    public void setCpm(Double cpm) {
        this.cpm = cpm;
    }

    public Double getCpv() {
        return cpv;
    }

    public void setCpv(Double cpv) {
        this.cpv = cpv;
    }

    public Double getCps() {
        return cps;
    }

    public void setCps(Double cps) {
        this.cps = cps;
    }

    public Double getCpa() {
        return cpa;
    }

    public void setCpa(Double cpa) {
        this.cpa = cpa;
    }

    public Double getCpc() {
        return cpc;
    }

    public void setCpc(Double cpc) {
        this.cpc = cpc;
    }

    public Double getTotal() {
        return cpa + cpc + cps + cpm + cpv;
    }
}
