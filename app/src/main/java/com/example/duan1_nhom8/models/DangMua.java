package com.example.duan1_nhom8.models;

public class DangMua implements Comparable<DangMua> {
    String iddonhang, idnguoidung;
    Boolean trangthai;
    public String ngaymua;
    Long tongsanpham, tongtien;

    public DangMua() {
    }

    public DangMua(String iddonhang, String idnguoidung, Boolean trangthai, String ngaymua, Long tongsanpham, Long tongtien) {
        this.iddonhang = iddonhang;
        this.idnguoidung = idnguoidung;
        this.trangthai = trangthai;
        this.ngaymua = ngaymua;
        this.tongsanpham = tongsanpham;
        this.tongtien = tongtien;
    }

    public String getIddonhang() {
        return iddonhang;
    }

    public void setIddonhang(String iddonhang) {
        this.iddonhang = iddonhang;
    }

    public String getIdnguoidung() {
        return idnguoidung;
    }

    public void setIdnguoidung(String idnguoidung) {
        this.idnguoidung = idnguoidung;
    }

    public Boolean getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(Boolean trangthai) {
        this.trangthai = trangthai;
    }

    public String getNgaymua() {
        return ngaymua;
    }

    public void setNgaymua(String ngaymua) {
        this.ngaymua = ngaymua;
    }

    public Long getTongsanpham() {
        return tongsanpham;
    }

    public void setTongsanpham(Long tongsanpham) {
        this.tongsanpham = tongsanpham;
    }

    public Long getTongtien() {
        return tongtien;
    }

    public void setTongtien(Long tongtien) {
        this.tongtien = tongtien;
    }

    @Override
    public String toString() {
        return "DangMua@iddonhang=" + iddonhang + ",idnguoidung=" + idnguoidung
                + ",trangthai=" + trangthai + ",ngaymua=" + ngaymua + ",tongsanpham=" + tongsanpham + ",tongtien=" + tongtien;
    }

    @Override
    public int compareTo(DangMua dangMua) {
        // sort student's name by ASC
        return this.getIddonhang().compareTo(dangMua.getIddonhang());
    }
}
