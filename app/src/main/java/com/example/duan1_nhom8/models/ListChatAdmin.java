package com.example.duan1_nhom8.models;

public class ListChatAdmin {
    String tennguoidung, id;

    public ListChatAdmin() {
    }

    public ListChatAdmin(String tennguoidung, String id) {
        this.tennguoidung = tennguoidung;
        this.id = id;
    }

    public String getTennguoidung() {
        return tennguoidung;
    }

    public void setTennguoidung(String tennguoidung) {
        this.tennguoidung = tennguoidung;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
