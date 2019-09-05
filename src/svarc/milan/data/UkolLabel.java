/*
 * Copyright (c) 2019. Created by Milan Švarc
 */

package svarc.milan.data;

import javafx.scene.control.Label;

public class UkolLabel extends Label {
    private String nadpis;
    private String poznamka;
    private String datum;

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public UkolLabel(String nadpis, String poznamka, String datum) {
        super(nadpis);
        this.nadpis = nadpis;
        this.poznamka = poznamka;
        this.datum = datum;
    }

    public String getNadpis() {
        return nadpis;
    }

    public void setNadpis(String nadpis) {
        this.nadpis = nadpis;
        this.setText(nadpis);
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }
}
