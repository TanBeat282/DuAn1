package com.example.duan1_nhom8.models;

public class KhuyenMai {
    String idUrlKhuyeMai, UrlKhuyenMai;

    public KhuyenMai() {
    }

    public KhuyenMai(String idUrlKhuyeMai, String urlKhuyenMai) {
        this.idUrlKhuyeMai = idUrlKhuyeMai;
        UrlKhuyenMai = urlKhuyenMai;
    }

    public String getIdUrlKhuyeMai() {
        return idUrlKhuyeMai;
    }

    public void setIdUrlKhuyeMai(String idUrlKhuyeMai) {
        this.idUrlKhuyeMai = idUrlKhuyeMai;
    }

    public String getUrlKhuyenMai() {
        return UrlKhuyenMai;
    }

    public void setUrlKhuyenMai(String urlKhuyenMai) {
        UrlKhuyenMai = urlKhuyenMai;
    }
}
