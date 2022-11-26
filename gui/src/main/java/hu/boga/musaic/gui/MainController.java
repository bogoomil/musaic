package hu.boga.musaic.gui;

import hu.boga.musaic.GuiceModule;
import hu.boga.musaic.gui.sequencemanager.SequenceManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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

    public void newProject() throws IOException {
        Stage newWindow = new Stage();
        newWindow.setTitle("new sequence");
        newWindow.setScene(new Scene(getSequenceTabController()));
        newWindow.initModality(Modality.NONE);

        Stage window = (Stage) mainmenu.getScene().getWindow();
        newWindow.initOwner(window);

        newWindow.show();
    }

    public void openFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if(file != null){
            openFile(file);
        }
    }

    private void openFile(File file) throws IOException {
        getSequenceTabController();
    }

    private void createNewTab() throws IOException {
//        getSequenceTabController().initSequence();
    }

    private BorderPane getSequenceTabController() throws IOException {
        FXMLLoader loader = new FXMLLoader(SequenceManager.class.getResource("sequence-manager.fxml"));
        loader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        BorderPane sequenceManagerPane =  loader.load();
        return sequenceManagerPane;
    }
}