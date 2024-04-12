package com.example.duan1_nhom8.models;

import java.io.Serializable;
import java.util.Date;

public class ThongTinCaNhan implements Serializable {
    String idnguoidung, tentaikhoan, email, diachi, url, ngaysinh, sdt;

    public ThongTinCaNhan() {
    }

    public ThongTinCaNhan(String idnguoidung, String tentaikhoan, String email, String diachi, String url, String ngaysinh, String sdt) {
        this.idnguoidung = idnguoidung;
        this.tentaikhoan = tentaikhoan;
        this.email = email;
        this.diachi = diachi;
        this.url = url;
        this.ngaysinh = ngaysinh;
        this.sdt = sdt;
    }

    public String getIdnguoidung() {
        return idnguoidung;
    }

    public void setIdnguoidung(String idnguoidung) {
        this.idnguoidung = idnguoidung;
    }

    public String getTentaikhoan() {
        return tentaikhoan;
    }

    public void setTentaikhoan(String tentaikhoan) {
        this.tentaikhoan = tentaikhoan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(String ngaysinh) {
        this.ngaysinh = ngaysinh;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }
}
