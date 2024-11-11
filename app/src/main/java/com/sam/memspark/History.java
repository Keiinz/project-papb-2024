package com.sam.memspark;

public class History {

    public String deckName;
    public String nilai;
    public String tanggal;

    public History(String deckName, String nilai, String tanggal) {
        this.deckName = deckName;
        this.nilai = nilai;
        this.tanggal = tanggal;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public String getNilai() {
        return nilai;
    }

    public void setNilai(String nilai) {
        this.nilai = nilai;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
