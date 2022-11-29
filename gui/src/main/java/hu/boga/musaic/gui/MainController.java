package hu.boga.musaic.gui;

import hu.boga.musaic.GuiceModule;
import hu.boga.musaic.gui.sequence.SequencePresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class MainController {

    public MenuBar mainmenu;

    @Inject
    public MainController() {
    }

    @FXML
    private void create(ActionEvent actionEvent) throws IOException {
        create(createSequence());
    }

    private Pane createSequence() throws IOException {
        FXMLLoader loader = initFXMLLoader();
        BorderPane pane =  loader.load();
        SequencePresenter presenter = loader.getController();
        presenter.create();
        return pane;
    }

    @FXML
    private void open(ActionEvent actionEvent) throws IOException {
        openFile();
    }

    private void create(Pane pane){
        Stage newWindow = new Stage();
        newWindow.setTitle("new sequence");
        newWindow.setScene(new Scene(pane));
        newWindow.initModality(Modality.NONE);
        Stage window = (Stage) mainmenu.getScene().getWindow();
        newWindow.initOwner(window);
        newWindow.show();
    }

    private FXMLLoader initFXMLLoader() {
        FXMLLoader loader = new FXMLLoader(SequencePresenter.class.getResource("sequence-view.fxml"));
        loader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        return loader;
    }

    private void openFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if(file != null){
            openSequence(file.getAbsolutePath());
        }
    }

    private void openSequence(String path) throws IOException {
        FXMLLoader loader = initFXMLLoader();
        BorderPane pane =  loader.load();
        SequencePresenter presenter = loader.getController();
        presenter.open(path);
        create(pane);
    }

}