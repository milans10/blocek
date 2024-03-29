/*
 * Copyright (c) 2019. Created by Milan Švarc
 */

package svarc.milan.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import svarc.milan.data.Databaze;
import svarc.milan.data.UkolLabel;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

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
                        "-fx-border-color: black;"
                );

                Label label = (Label) gridPane.getChildren().get(0);
                label.setText(String.valueOf(den));
                // Označení (zvýraznění) aktuálního dne v kalendáři
                if (den == dneska.getDayOfMonth() && dneska.getMonthValue() == LocalDate.now().getMonthValue() && dneska.getYear() == LocalDate.now().getYear()) {
                    label.setStyle("-fx-background-color: #ff4337;" +
                            "-fx-background-radius: 5 5 0 0;" +
                            "-fx-effect: innershadow( three-pass-box , rgba(0,0,0,0.5) , 5, 0.0 , 0 , -2 );"
                    );
                    label.setOnMouseExited(mouseEvent -> label.setStyle("-fx-background-color: #ff4337;" +
                            "-fx-background-radius: 5 5 0 0;" +
                            "-fx-effect: innershadow( three-pass-box , rgba(0,0,0,0.5) , 5, 0.0 , 0 , -2 );"
                    ));
                } else {
                    label.setStyle("-fx-background-color: #166cbf;" +
                            "-fx-effect: innershadow( three-pass-box , rgba(0,0,0,0.5) , 5, 0.0 , 0 , -2 );"
                    );
                    label.setOnMouseExited(mouseEvent -> label.setStyle("-fx-background-color: #166cbf;" +
                            "-fx-effect: innershadow( three-pass-box , rgba(0,0,0,0.5) , 5, 0.0 , 0 , -2 );"
                    ));
                }
                den++;

                label.setOnMouseEntered(mouseEvent -> label.setStyle("-fx-background-color: #ff4337;" +
                        "-fx-border-color: linear-gradient(from 0% 0% to 100% 0%, black, barvaZvirazni 50%, black 100%, black);" +
                        "-fx-border-style: solid inside;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-insets: -2;" +
                        "-fx-background-insets: 0;" +
                        "-fx-text-fill: #000000;" +
                        "-fx-effect: innershadow( three-pass-box , rgba(0,0,0,0.5) , 5, 0.0 , 0 , -2 );" +
                        ""));

                label.setTooltip(new Tooltip(">> Klikněte pro zadání úkolu pro den: " + label.getText() + "." + dneska.getMonthValue() + "." + dneska.getYear() + " <<"));

                VBox vBox = new VBox();

                ScrollPane scrollPane = (ScrollPane) gridPane.getChildren().get(1);
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefViewportWidth(100);
                scrollPane.setPrefViewportHeight(200);
                scrollPane.setContent(vBox);
                scrollPane.setStyle("-fx-background: #ffffff;" +
                        "-fx-effect: innershadow( three-pass-box , rgba(0,0,0,0.5) , 5, 0.0 , -2 , -2 );"
                );

                vBox.getChildren().clear();

                for (UkolLabel ukolLabel : databaze.getDataKalendare()) {
                    if (ukolLabel.getDatum().equals((label.getText() + "." + dneska.getMonthValue() + "." + dneska.getYear()))) {
                        ukolLabel.setStyle("-fx-background-color: lightgreen;" +
                                "-fx-text-fill: #000000;" +
                                "-fx-max-width: 500;");
                        if (ukolLabel.isSplneno()) {
                            ukolLabel.setId("preskrtnuty");
                        }
                        ukolLabel.setOnMouseClicked(mouseEvent -> {
                            ukazPoznamkaDialog(ukolLabel);
                            if (ukolLabel.getText().isEmpty()) {
                                vBox.getChildren().remove(ukolLabel);
                            }
                            aktualizujVzhledKalendare();
                        });
                        ukolLabel.setOnMouseEntered(mouseEvent -> {
                            ukolLabel.setStyle("-fx-background-color: lightgreen;" +
                                    "-fx-text-fill: #000000;" +
                                    "-fx-max-width: 500;" +
                                    "-fx-border-color: linear-gradient(from 0% 0% to 100% 0%, black, barvaZvirazni 50%, black 100%, black);" +
                                    "-fx-border-style: solid inside;" +
                                    "-fx-border-width: 1.5;" +
                                    "-fx-background-insets: 0;");
                            ukolLabel.setTooltip(new Tooltip(ukolLabel.getText() + "\n >> Klikněte pro editaci <<"));
                            ukolLabel.getTooltip().setId("nepreskrtnuty");
                        });

                        ukolLabel.setOnMouseExited(mouseEvent -> {
                            ukolLabel.setStyle("-fx-background-color: lightgreen;" +
                                    "-fx-text-fill: #000000;" +
                                    "-fx-max-width: 500;");
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
                scrollPane.setStyle("-fx-background-color: none;" +
                        "-fx-effect: innershadow( three-pass-box , rgba(0,0,0,0.0) , 0, 0.0 , 0 , 0 );"
                );

                VBox vBox = (VBox) scrollPane.getContent();
                if (vBox != null) {
                    vBox.getChildren().clear();
                    vBox.setStyle("-fx-background-color: transparent;");
                }
            }

        }
        mesicJmeno.setText(dneska.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("cs")).toUpperCase());
        rokJmeno.setText(String.valueOf(dneska.getYear()));
    }

    private void pridejPoznamku(VBox vBox, int tentoden) {
        zadavaciDialog(vBox, tentoden);
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
        alert.setContentText("Naprogramoval: Milan Švarc\nVerze: 1.01");
        alert.showAndWait();
    }

    /**
     * Slouží k zobrazení zadávacího dialogu.
     *
     * @param vBox     prostor pro vybraný den v měsíci do kterého vkládáme Úkol(položku)
     * @param tentoden číslo dne v měsíci do kterého přidáváme poznámku
     */
    private void zadavaciDialog(VBox vBox, int tentoden) {
        String kteryDen = (tentoden) + "." + dneska.getMonthValue() + "." + dneska.getYear();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("infoDialog.fxml"));
        Parent parent = null;
        try {
            parent = parent = fxmlLoader.load();
            InfoDialogController dialogController = fxmlLoader.getController();
            dialogController.posliData("Zadáváte událost ke dni: " + kteryDen, new UkolLabel("", "", kteryDen, false));
            dialogController.vypniTlacitko(true);

            Scene scene = new Scene(parent, 400, 600);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            File f = new File(System.getProperty("user.dir") + "\\src\\svarc\\milan\\vzhled.css");
            scene.getStylesheets().clear();
            scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
            stage.setTitle("Zadejte událost...");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        aktualizujVzhledKalendare();
    }

    /**
     * Slouží k úpravě nebo odstranění úkolu(položky). Zobrazí dialogové okno, kde můžeme provádět změny.
     *
     * @param ukolLabel Aktuální úkol(položka), kterou chceme upravit/odstranit
     */
    private void ukazPoznamkaDialog(UkolLabel ukolLabel) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("infoDialog.fxml"));
        Parent parent = null;
        try {
            parent = parent = fxmlLoader.load();
            InfoDialogController dialogController = fxmlLoader.getController();
            dialogController.posliData("Upravujete událost ze dne: " + ukolLabel.getDatum(), ukolLabel);

            Scene scene = new Scene(parent, 400, 600);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            File f = new File(System.getProperty("user.dir") + "\\src\\svarc\\milan\\vzhled.css");
            scene.getStylesheets().clear();
            scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
            stage.setTitle("Detail úkolu...");
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
