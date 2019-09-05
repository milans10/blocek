/*
 * Copyright (c) 2019. Created by Milan Švarc
 */

package svarc.milan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {
    private String cesta = System.getProperty("user.dir");

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("gui/kalendar.fxml"));
        primaryStage.setTitle("Bloček");
        Scene scene = new Scene(root, 670, 670);
        primaryStage.showingProperty().addListener((observable, oldValue, showing) -> {
            if (showing) {
                primaryStage.setMinHeight(primaryStage.getHeight());
                primaryStage.setMinWidth(primaryStage.getWidth());
            }
        });
        primaryStage.setScene(scene);
        File f = new File(cesta+"\\src\\svarc\\milan\\vzhled.css");
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
