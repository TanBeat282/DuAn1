package com.example.duan1_nhom8.models;

import java.util.Date;

public class DonHang {
    String idsach,tensach, url;
    Long soluong, giaban;

    public DonHang() {
    }

    public DonHang(String idsach, String tensach, String url, Long soluong, Long giaban) {
        this.idsach = idsach;
        this.tensach = tensach;
        this.url = url;
        this.soluong = soluong;
        this.giaban = giaban;
    }

    public String getIdsach() {
        return idsach;
    }

    public void setIdsach(String idsach) {
        this.idsach = idsach;
    }

    public String getTensach() {
        return tensach;
    }

    public void setTensach(String tensach) {
        this.tensach = tensach;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getSoluong() {
        return soluong;
    }

    public void setSoluong(Long soluong) {
        this.soluong = soluong;
    }

    public Long getGiaban() {
        return giaban;
    }

    public void setGiaban(Long giaban) {
        this.giaban = giaban;
    }
}
