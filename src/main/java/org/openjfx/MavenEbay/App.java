package org.openjfx.MavenEbay;

import java.io.IOException;

import org.openjfx.MavenEbay.controllers.FXMLController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage stage) throws IOException {
        FXMLController controller = new FXMLController();
    	logger.info("loadFXML");
        scene = new Scene(loadFXML("ebaytoolui", controller), 500, 600);
        stage.setScene(scene);
        stage.setOnShown(event -> {
        	logger.debug("initValue");
			controller.initValue();
		});
        
        stage.show();
    }

    private static Parent loadFXML(String fxml, FXMLController controller ) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    	logger.info(fxmlLoader.toString());
        fxmlLoader.setController(controller);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}