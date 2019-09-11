/*
 * Copyright (c) 2019. Created by Milan Švarc
 */

package svarc.milan.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import svarc.milan.data.Databaze;
import svarc.milan.data.UkolLabel;

public class InfoDialogController extends Dialog<UkolLabel> {

    @FXML
    private TextField predmet;
    @FXML
    private TextArea poznamka;
    @FXML
    private ToggleButton tbtnSplneno;
    private Databaze databaze = Databaze.getKalendar();
    private UkolLabel docasnyUkolLabel;

    /**
     * Uloží úkol do databáze a zavře dialogové okno.
     *
     * @param event zdroj události
     */
    @FXML
    void ulozUkol(ActionEvent event) {
        databaze.vymazatUkol(docasnyUkolLabel);
        databaze.vlozUkolDoDatabaze(new UkolLabel(predmet.getText(), poznamka.getText(), docasnyUkolLabel.getDatum(), tbtnSplneno.isSelected()));
        closeStage(event);
    }

    /**
     * Vymaže úkol z databáze a zavře dialogové okno
     *
     * @param event zdroj události
     */
    @FXML
    void odstranUkol(ActionEvent event) {
        databaze.vymazatUkol(docasnyUkolLabel);
        closeStage(event);
    }

    /**
     * Slouží k zavření dialogového okna
     *
     * @param event zdroj události
     */
    @FXML
    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Prosté prohození názvu dle statusu tlačítka.
     */
    @FXML
    public void splneno() {
        if (tbtnSplneno.isSelected()) {
            tbtnSplneno.setText("Splněno");
        } else {
            tbtnSplneno.setText("Nesplněno");
        }
    }

    void posliData(UkolLabel ukolLabel) {
        docasnyUkolLabel = ukolLabel;
        predmet.setText(ukolLabel.getPredmet());
        poznamka.setText(ukolLabel.getPoznamka());
        tbtnSplneno.setSelected(ukolLabel.isSplneno());
        splneno();
    }

}
