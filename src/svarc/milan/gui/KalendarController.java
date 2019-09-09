/*
 * Copyright (c) 2019. Created by Milan Švarc
 */

package svarc.milan.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import svarc.milan.data.Databaze;
import svarc.milan.data.UkolLabel;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

public class KalendarController {

    private Databaze databaze = Databaze.getKalendar();
    private LocalDate dneska;

    @FXML
    private GridPane gpKalendar;
    @FXML
    private Label mesicJmeno;
    @FXML
    private Label rokJmeno;

    public void initialize() {
        this.dneska = LocalDate.now();
        databaze.vytvorTabulkuKalendar();
        aktualizujVzhledKalendare();
    }

    /**
     * Aktualizuje aktuální měsíc, který je viditelný
     */
    private void aktualizujVzhledKalendare() {
        int denvTydnu = LocalDate.of(dneska.getYear(), dneska.getMonthValue(), 1).getDayOfWeek().getValue() - 1;
        int posledniDen = dneska.getMonth().length(dneska.isLeapYear());

        int den = 1;
        for (int i = 7; i < 49; i++) {//49 max

            if ((i >= (7 + denvTydnu)) && (i < (7 + denvTydnu + posledniDen))) {

                GridPane gridPane;
                gridPane = (GridPane) gpKalendar.getChildren().get(i);
                gridPane.setStyle("-fx-padding: 0;" +
                        "-fx-border-style: solid inside;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-color: black;");

                Label label = (Label) gridPane.getChildren().get(0);
                label.setText(String.valueOf(den++));
                label.setStyle("-fx-background-color: #166cbf;");
                label.setOnMouseEntered(mouseEvent -> label.setStyle("-fx-background-color: #ff4337;"));
                label.setOnMouseExited(mouseEvent -> label.setStyle("-fx-background-color: #166cbf;"));
                label.setTooltip(new Tooltip(">> Kliknětě pro zadání úkolu pro den: " + label.getText() + "." + dneska.getMonthValue() + "." + dneska.getYear() + " <<"));

                VBox vBox = new VBox();

                ScrollPane scrollPane = (ScrollPane) gridPane.getChildren().get(1);
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefViewportWidth(100);
                scrollPane.setPrefViewportHeight(200);
                scrollPane.setContent(vBox);
                scrollPane.setStyle("-fx-background-color: none;");

                vBox.getChildren().clear();

                for (UkolLabel ukolLabel : databaze.getDataKalendare()) {
                    if (ukolLabel.getDatum().equals((label.getText() + "." + dneska.getMonthValue() + "." + dneska.getYear()))) {
                        ukolLabel.setStyle("-fx-background-color: lightgreen;" +
                                "-fx-max-width: " + 500 + ";");
                        ukolLabel.setOnMouseClicked(mouseEvent -> {
                            ukazPoznamkaDialog(ukolLabel);
                            if (ukolLabel.getText().isEmpty()) {
                                vBox.getChildren().remove(ukolLabel);
                            }
                        });
                        ukolLabel.setOnMouseEntered(mouseEvent -> {
                            ukolLabel.setTooltip(new Tooltip(ukolLabel.getText() + "\n >> Klikněte pro editaci <<"));
                        });
                        vBox.getChildren().add(ukolLabel);
                    }
                }

                label.setOnMouseClicked(mouseEvent -> {
                    int tentoden = Integer.parseInt(label.getText());
                    pridejPoznamku(vBox, tentoden);
                });


            } else {
                GridPane gridPane = (GridPane) gpKalendar.getChildren().get(i);
                gridPane.setStyle("-fx-padding: 0;" +
                        "-fx-border-style: none;");
                Label label = (Label) gridPane.getChildren().get(0);
                label.setText(null);
                label.setStyle("-fx-background-color: none;");

                label.setOnMouseEntered(mouseEvent -> {
                });
                label.setOnMouseExited(mouseEvent -> {
                });
                label.setOnMouseClicked(mouseEvent -> {
                });

                ScrollPane scrollPane = (ScrollPane) gridPane.getChildren().get(1);
                scrollPane.setStyle("-fx-background-color: none;");

                VBox vBox = (VBox) scrollPane.getContent();
                if (vBox != null) vBox.getChildren().clear();
            }

        }
        mesicJmeno.setText(dneska.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("cs")).toUpperCase());
        rokJmeno.setText(String.valueOf(dneska.getYear()));
    }

    private void pridejPoznamku(VBox vBox, int tentoden) {
        zadavacíDialog(vBox, tentoden);
    }

    public void predchoziMesic() {
        dneska = dneska.minusMonths(1);
        aktualizujVzhledKalendare();
    }

    public void nasledujiciMesic() {
        dneska = dneska.plusMonths(1);
        aktualizujVzhledKalendare();
    }

    public void konecProgramu() {
        Platform.exit();
    }

    public void aboutInformaceDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("O programu");
        alert.setHeaderText("Program Bloček");
        alert.setContentText("Naprogramoval: Milan Švarc\nVerze: 1.0 beta");
        alert.showAndWait();
    }

    /**
     * Slouží k zobrazení zadávacího dialogu.
     *
     * @param vBox     prostor pro vybraný den v měsíci do kterého vkládáme Úkol(položku)
     * @param tentoden číslo dne v měsíci do kterého přidáváme poznámku
     */
    private void zadavacíDialog(VBox vBox, int tentoden) {
        String kteryDen = (tentoden) + "." + dneska.getMonthValue() + "." + dneska.getYear();

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Zadejte událost...");
        dialog.setHeaderText("Zadejte událost ke dni: " + kteryDen);

        ButtonType tlacitkoOKtyp = new ButtonType("Potvrdit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(tlacitkoOKtyp, ButtonType.CANCEL);

        GridPane grid = new GridPane();

        TextField predmet = new TextField();
        predmet.setPromptText("Zadejte předmět");
        TextArea poznamka = new TextArea();
        poznamka.setPromptText("Zadejte bližší informace");

        grid.add(new Label("Předmět:"), 0, 0);
        grid.add(predmet, 1, 0);
        grid.add(new Label("Poznámka:"), 0, 1);
        grid.add(poznamka, 1, 1);

        Node tlacitkoOK = dialog.getDialogPane().lookupButton(tlacitkoOKtyp);
        tlacitkoOK.setDisable(true);

        predmet.textProperty().addListener((observable, oldValue, newValue) -> {
            tlacitkoOK.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(predmet::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == tlacitkoOKtyp) {
                return new Pair<>(predmet.getText(), poznamka.getText());
            }
            return null;
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(predmetPoznamka -> {
            UkolLabel ukolLabel = new UkolLabel(predmet.getText(), poznamka.getText(), kteryDen);
            ukolLabel.setStyle("-fx-background-color: lightgreen;" +
                    "-fx-max-width: " + grid.getWidth() + ";");
            ukolLabel.setOnMouseClicked(mouseEvent -> {
                ukazPoznamkaDialog(ukolLabel);
                if (ukolLabel.getText().isEmpty()) {
                    vBox.getChildren().remove(ukolLabel);
                }
            });
            ukolLabel.setOnMouseEntered(mouseEvent -> {
                ukolLabel.setTooltip(new Tooltip(ukolLabel.getText() + "\n >> Klikněte pro editaci <<"));
            });
            databaze.vlozUkolDoDatabaze(ukolLabel);
            vBox.getChildren().add(ukolLabel);

        });

    }

    /**
     * Slouží k úpravě nebo odstranění úkolu(položky).
     *
     * @param ukolLabel Aktuální úkol(položka), kterou chceme upravit/odstranit
     */
    private void ukazPoznamkaDialog(UkolLabel ukolLabel) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Detail události...");
        dialog.setHeaderText("");

        ButtonType tlacitkoOKtyp = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType tlacitkoOdstranit = new ButtonType("Odstranit");
        dialog.getDialogPane().getButtonTypes().addAll(tlacitkoOKtyp, tlacitkoOdstranit, ButtonType.CANCEL);

        GridPane grid = new GridPane();

        TextField predmet = new TextField(ukolLabel.getNadpis());
        predmet.setPromptText("Zadejte předmět");
        TextArea poznamka = new TextArea(ukolLabel.getPoznamka());
        poznamka.setPromptText("Zadejte bližší informace");

        grid.add(new Label("Předmět:"), 0, 0);
        grid.add(predmet, 1, 0);
        grid.add(new Label("Poznámka:"), 0, 1);
        grid.add(poznamka, 1, 1);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(predmet::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == tlacitkoOKtyp) {
                return new Pair<>(predmet.getText(), poznamka.getText());
            }
            if (dialogButton == tlacitkoOdstranit) {
                databaze.vymazatUkol(ukolLabel);
                ukolLabel.setText("");
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(predmetPoznamka -> {
            databaze.vymazatUkol(ukolLabel);
            ukolLabel.setNadpis(predmet.getText());
            ukolLabel.setPoznamka(poznamka.getText());
            databaze.vlozUkolDoDatabaze(ukolLabel);
        });
    }
}
