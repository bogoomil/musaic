package hu.boga.musaic.gui;

import hu.boga.musaic.GuiceModule;
import hu.boga.musaic.gui.sequenceeditor.SequenceEditor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class MainController {

    @FXML
    public TabPane mainTab;

    @Inject
    public MainController() {
    }

    public void newProject(ActionEvent actionEvent) throws IOException {
        createNewTab();
    }

    public void openFile(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if(file != null){
            openFile(file);
        }
    }

    private void openFile(File file) throws IOException {
        getSequenceTabController(file.getName()).initSequence(file);
    }

    private void createNewTab() throws IOException {
        getSequenceTabController("New sequence").initSequence();
    }

    private SequenceEditor getSequenceTabController(String tabName) throws IOException {
        FXMLLoader loader = new FXMLLoader(SequenceEditor.class.getResource("sequence-editor.fxml"));
        loader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        BorderPane sequenceEditorPanel =  loader.load();
        SequenceEditor controller = loader.getController();
        mainTab.getTabs().add(new Tab(tabName, sequenceEditorPanel));
        return controller;
    }
}