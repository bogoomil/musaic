package hu.boga.musaic.gui.sequence;

import hu.boga.musaic.gui.sequence.components.ChannelMappingChangeListener;
import hu.boga.musaic.gui.sequence.components.ChannelMappingManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class SequencePresenter implements ChannelMappingChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(SequencePresenter.class);

    @FXML
    private AnchorPane channelMappingPane;
    private FileChooser fileChooser = new FileChooser();

    private final SequenceService service;
    private SequenceModell modell;
    private String path;

    @Inject
    public SequencePresenter(SequenceService service) {
        this.service = service;
    }

    public void initialize() {
    }

    public void open(String path) {
        this.path = path;
        service.open(path);
        this.modell = service.getSequence();
        initGui();
    }

    public void create() {
        service.create();
        this.modell = service.getSequence();
        initGui();
    }

    private void initGui(){
        displayChannelMapping();
    }

    private void displayChannelMapping() {
        ChannelMappingManager channelMappingManager = new ChannelMappingManager(modell);
        channelMappingManager.addChannelMappingChangeListener(this);
        channelMappingPane.getChildren().add(channelMappingManager.create());
    }

    @FXML
    private void saveSequence(ActionEvent actionEvent) {
        if(path != null){
            service.save(modell.id, path);
        } else {
            saveAsSequence(actionEvent);
        }
    }

    @FXML
    private void saveAsSequence(ActionEvent actionEvent) {
        path = fileChooser.showSaveDialog(null).getAbsolutePath();
        if(path != null){
            saveSequence(actionEvent);
        }
    }

    @FXML
    private void addTrack(ActionEvent actionEvent) {
    }

    @Override
    public void mappingChanged(int channel, int program) {
        service.updateChannelMapping(modell.id, channel, program);
    }
}
