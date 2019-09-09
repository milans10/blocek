/*
 * Copyright (c) 2019. Created by Milan Švarc
 */

package svarc.milan.data;

import javafx.scene.control.Label;

public class UkolLabel extends Label {
    private String predmet;
    private String poznamka;
    private String datum;
    private boolean splneno;

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public UkolLabel(String predmet, String poznamka, String datum) {
        super(predmet);
        this.predmet = predmet;
        this.poznamka = poznamka;
        this.datum = datum;
        this.splneno = false;
    }

    public UkolLabel(String predmet, String poznamka, String datum, boolean splneno) {
        super(predmet);
        this.predmet = predmet;
        this.poznamka = poznamka;
        this.datum = datum;
        this.splneno = splneno;
    }

    public String getPredmet() {
        return predmet;
    }

    public void setPredmet(String predmet) {
        this.predmet = predmet;
        this.setText(predmet);
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }

    public boolean isSplneno() {
        return splneno;
    }

    public void setSplneno(boolean splneno) {
        this.splneno = splneno;
    }
}
