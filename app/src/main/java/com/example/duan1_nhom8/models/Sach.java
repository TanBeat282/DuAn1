package com.example.duan1_nhom8.models;

import java.io.Serializable;

public class Sach implements Serializable {
    String id, tensach, tacgia, loaisach, nhaxuatban, giaban, url;


    public Sach(String id, String tensach, String tacgia, String loaisach, String nhaxuatban, String giaban, String url) {
        this.id = id;
        this.tensach = tensach;
        this.tacgia = tacgia;
        this.loaisach = loaisach;
        this.nhaxuatban = nhaxuatban;
        this.giaban = giaban;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTensach() {
        return tensach;
    }

    public void setTensach(String tensach) {
        this.tensach = tensach;
    }

    public String getTacgia() {
        return tacgia;
    }

    public void setTacgia(String tacgia) {
        this.tacgia = tacgia;
    }

    public String getLoaisach() {
        return loaisach;
    }

    public void setLoaisach(String loaisach) {
        this.loaisach = loaisach;
    }

    public String getNhaxuatban() {
        return nhaxuatban;
    }

    public void setNhaxuatban(String nhaxuatban) {
        this.nhaxuatban = nhaxuatban;
    }

    public String getGiaban() {
        return giaban;
    }

    public void setGiaban(String giaban) {
        this.giaban = giaban;
    }
}
