package hu.boga.musaic.gui.sequence;

import hu.boga.musaic.gui.sequence.components.ChannelMappingChangeListener;
import hu.boga.musaic.gui.sequence.components.ChannelMappingManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class SequencePresenter implements ChannelMappingChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(SequencePresenter.class);

    @FXML
    private AnchorPane channelMappingPane;
    @FXML
    private Label label;


    private final SequenceService service;
    private SequenceModell modell;

    @Inject
    public SequencePresenter(SequenceService service) {
        this.service = service;
    }

    public void initialize() {
    }

    public void open(String path) {
        service.open(path);
        this.modell = new SequenceDtoToModellConverter(service.getSequenceDto()).convert();
        initGui();
    }

    public void create() {
        service.create();
        this.modell = new SequenceDtoToModellConverter(service.getSequenceDto()).convert();
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
    }

    @FXML
    private void saveAsSequence(ActionEvent actionEvent) {
    }

    @FXML
    private void addTrack(ActionEvent actionEvent) {
    }

    @Override
    public void mappingChanged(int channel, int program) {
        service.updateChannelMapping(modell.id, channel, program);
    }
}
