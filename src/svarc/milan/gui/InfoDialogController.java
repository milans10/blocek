/*
 * Copyright (c) 2019. Created by Milan Švarc
 */

package svarc.milan.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import svarc.milan.data.Databaze;
import svarc.milan.data.UkolLabel;

public class InfoDialogController extends Dialog<UkolLabel> {
    @FXML
    private Label lblNadpis;
    @FXML
    private TextField tfPredmet;
    @FXML
    private TextArea taPoznamka;
    @FXML
    private Button btnOdstranUkol;
    @FXML
    private Button btnUlozUkol;
    @FXML
    private ToggleButton tbtnSplneno;

    private Databaze databaze = Databaze.getKalendar();
    private UkolLabel docasnyUkolLabel;

    public void initialize() {
        btnUlozUkol.setDisable((tfPredmet.getText().length() < 1));
        tfPredmet.textProperty().addListener((observable, oldValue, newValue) -> btnUlozUkol.setDisable((tfPredmet.getText().length() < 1)));
        tfPredmet.requestFocus();
    }

    /**
     * Uloží úkol do databáze a zavře dialogové okno.
     *
     * @param event zdroj události
     */
    @FXML
    void ulozUkol(ActionEvent event) {
        databaze.vymazatUkol(docasnyUkolLabel);
        databaze.vlozUkolDoDatabaze(new UkolLabel(tfPredmet.getText(), taPoznamka.getText(), docasnyUkolLabel.getDatum(), tbtnSplneno.isSelected()));
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

    /**
     * Slouží k přenosu dat do dialogu.
     * @param nadpis libovolný text který se zobrazí v napisovém Labelu
     * @param ukolLabel
     */
    void posliData(String nadpis, UkolLabel ukolLabel) {
        lblNadpis.setText(nadpis);
        docasnyUkolLabel = ukolLabel;
        tfPredmet.setText(ukolLabel.getPredmet());
        taPoznamka.setText(ukolLabel.getPoznamka());
        tbtnSplneno.setSelected(ukolLabel.isSplneno());
        splneno();
    }

    void vypniTlacitko(Boolean odstranitUkol) {
        btnOdstranUkol.setDisable(odstranitUkol);
    }

}
