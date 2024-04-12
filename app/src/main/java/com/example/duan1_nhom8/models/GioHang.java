package com.example.duan1_nhom8.models;

public class GioHang {
    String idSach,tenSach, url;
    Long soLuong, giaBan;

    public GioHang() {
    }

    public GioHang(String idSach, String tenSach, String url, Long soLuong, Long giaBan) {
        this.idSach = idSach;
        this.tenSach = tenSach;
        this.url = url;
        this.soLuong = soLuong;
        this.giaBan = giaBan;
    }

    public String getIdSach() {
        return idSach;
    }

    public void setIdSach(String idSach) {
        this.idSach = idSach;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Long soLuong) {
        this.soLuong = soLuong;
    }

    public Long getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(Long giaBan) {
        this.giaBan = giaBan;
    }
}
