package hu.boga.musaic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;

import static java.lang.System.exit;

public class MusaicApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        URL fxmlLocation = MainController.class.getResource("main-view.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlLocation);

//        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("main-view.fxml"));
        loader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("midiai");
        stage.setScene(scene);
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> exit(0));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}