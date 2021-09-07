package org.openjfx.MavenEbay.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DialogController {
	
    public static void showAlertWithoutHeaderText(String text) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Warning alert");
         alert.setHeaderText(null);
        alert.setContentText(text);
 
        alert.showAndWait();
    }
}
