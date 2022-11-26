package hu.boga.musaic.gui.sequencemanager;

import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;

public class SequenceManager implements SequenceBoundaryOut {
    private static final Logger LOG = LoggerFactory.getLogger(SequenceManager.class);
    public MenuBar mainmenu;


    private String filePath;
    private SequenceDto dto;

    private boolean initialized;

    SequenceBoundaryIn boundaryIn;
    private FileChooser fileChooser = new FileChooser();


    @Inject
    public SequenceManager(SequenceBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
    }

    @Override
    public void displaySequence(SequenceDto dto) {
        LOG.debug("sequence id: {}", dto.id);
        this.dto = dto;
        setTitle(dto.name);
    }

    void setTitle(String title){
        if(initialized){
            Scene scene = mainmenu.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.setTitle(dto.name != null ? dto.name : dto.id);
        }
    }


    public void initialize() {
        LOG.debug("initializing sequence manager....");
        boundaryIn.create();
        initialized = true;
    }

    public void openSequence(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            filePath = file.getAbsolutePath();
            boundaryIn.open(filePath);
        }
    }

    public void saveSequence(ActionEvent actionEvent) {
        if (filePath != null) {
            boundaryIn.save(dto.id, filePath);
        } else {
            saveAsSequence(actionEvent);
        }
    }

    public void saveAsSequence(ActionEvent actionEvent) {
        File file = fileChooser.showSaveDialog(null);
        filePath = file.getAbsolutePath();
        if (file != null) {
            boundaryIn.save(dto == null ? null : dto.id, filePath);
        }
    }
}
