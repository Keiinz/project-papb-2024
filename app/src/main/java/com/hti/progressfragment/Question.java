package com.hti.progressfragment;

public class Question {
    private String teksPertanyaan;
    private String[] opsi;
    private int indeksJawabanBenar;

    public Question(String teksPertanyaan, String[] opsi, int indeksJawabanBenar) {
        this.teksPertanyaan = teksPertanyaan;
        this.opsi = opsi;
        this.indeksJawabanBenar = indeksJawabanBenar;
    }

    public String getTeksPertanyaan() {
        return teksPertanyaan;
    }

    public String[] getOpsi() {
        return opsi;
    }

    public int getIndeksJawabanBenar() {
        return indeksJawabanBenar;
    }
}