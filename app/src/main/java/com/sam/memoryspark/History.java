package com.sam.memoryspark;

import java.io.Serializable;

public class History implements Serializable {
    private String deckName;
    private String tanggal;
    private String nilai;
    private int jumlahBenar;
    private int jumlahSalah;

    public History() {
    }

    public History(String deckName, String tanggal, String nilai, int jumlahBenar, int jumlahSalah) {
        this.deckName = deckName;
        this.tanggal = tanggal;
        this.nilai = nilai;
        this.jumlahBenar = jumlahBenar;
        this.jumlahSalah = jumlahSalah;
    }

    // Getter dan Setter
    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getNilai() {
        return nilai;
    }

    public void setNilai(String nilai) {
        this.nilai = nilai;
    }

    public int getJumlahBenar() {
        return jumlahBenar;
    }

    public void setJumlahBenar(int jumlahBenar) {
        this.jumlahBenar = jumlahBenar;
    }

    public int getJumlahSalah() {
        return jumlahSalah;
    }

    public void setJumlahSalah(int jumlahSalah) {
        this.jumlahSalah = jumlahSalah;
    }
}