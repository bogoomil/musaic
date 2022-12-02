package hu.boga.musaic.gui;

import hu.boga.musaic.gui.sequence.SequencePresenter;
import hu.boga.musaic.gui.sequence.SequencePresenterFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MainController {

    public MenuBar mainmenu;

    private final SequencePresenterFactory sequencePresenterFactory;

    @Inject
    public MainController(SequencePresenterFactory sequencePresenterFactory) {
        this.sequencePresenterFactory = sequencePresenterFactory;
    }

    @FXML
    private void createWindow(ActionEvent actionEvent) throws IOException {
        createSequencePresenter(null);
    }

    @FXML
    private void open(ActionEvent actionEvent) throws IOException {
        openFile().ifPresent(path -> {
            createSequencePresenter(path);
        });
    }

    private void createSequencePresenter(String path) {
        FXMLLoader loader = new FXMLLoader(SequencePresenter.class.getResource("sequence-view.fxml"));
        loader.setControllerFactory(c -> sequencePresenterFactory.create(path));
        createWindow(tryLoadingPane(loader));
    }

    private Pane tryLoadingPane(FXMLLoader loader) {
        BorderPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
    }

    private void createWindow(Pane pane) {
        Stage newWindow = new Stage();
        newWindow.setScene(new Scene(pane));
        newWindow.initModality(Modality.NONE);
        Stage window = (Stage) mainmenu.getScene().getWindow();
        newWindow.initOwner(window);
        newWindow.show();
    }

    private Optional<String> openFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) return Optional.of(file.getAbsolutePath());
        return Optional.empty();
    }
}