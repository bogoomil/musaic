package hu.boga.musaic.gui.sequenceeditor;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.GuiceModule;
import hu.boga.musaic.core.sequence.boundary.dtos.TrackDto;
import hu.boga.musaic.gui.controls.ModeCombo;
import hu.boga.musaic.gui.controls.NoteNameCombo;
import hu.boga.musaic.gui.controls.TempoSlider;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.gui.trackeditor.TrackEditor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class SequenceEditor implements SequenceBoundaryOut {
    private static final String DEFAULT_NAME = "new_project.mid";
    private static final Logger LOG = LoggerFactory.getLogger(SequenceEditor.class);
    private final SequenceBoundaryIn boundaryIn;

    public Label division;
    public Label resolution;
    public Label tickLength;
    public Label ticksPerMeasure;
    public Label ticksIn32nds;
    public Label ticksPerSecond;
    public Label tickSize;
    public Label tempoLabel;

    public TempoSlider tempoSlider;
    public NoteNameCombo rootCombo;
    public ModeCombo modeCombo;
    public Button btnClearMode;

    public final EventBus eventBus = new EventBus();

    @FXML
    private TextField tfFilename;
    @FXML
    private Accordion accordion;

    private String sequenceId;

    @Inject
    public SequenceEditor(SequenceBoundaryIn boundaryInProvider) {
        this.boundaryIn = boundaryInProvider;
        eventBus.register(this);
    }

    public void initialize() {
//        tempoSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
//                    initTemposSettings(newValue);
//                }
//        );
//        rootCombo.addEventHandler(ActionEvent.ACTION, event -> eventBus.post(new RootChangedEvent(rootCombo.getSelectedNoteName())));
//        modeCombo.addEventHandler(ActionEvent.ACTION, event -> eventBus.post(new ModeChangedEvent(modeCombo.getSelectedTone())));
//
//        btnClearMode.setOnAction(event -> {
//            modeCombo.getSelectionModel().clearSelection();
//            eventBus.post(new ModeChangedEvent(null));
//
//            rootCombo.getSelectionModel().clearSelection();
//            eventBus.post(new RootChangedEvent(null));
//        });
    }

    private void initTemposSettings(Number newValue) {
//        tempoLabel.setText("Tempo: " + newValue.intValue());
//        if(sequenceId != null){
//            boundaryIn.setTempo(sequenceId, newValue.intValue());
//        }
    }

    public void saveSequence(ActionEvent actionEvent) {
//        LOG.debug("Saving sequence: " + sequenceId);
//        String path = new FileChooser().showSaveDialog(null).getAbsolutePath();
//        this.boundaryIn.save(sequenceId, path);
    }

    public void onPlayCurrentSec(ActionEvent actionEvent) {
        boundaryIn.play(sequenceId);
    }

//    public void stopPlayback(ActionEvent actionEvent) {
//        this.boundaryIn.stop(sequenceId);
//    }

    @Override
    public void displaySequence(SequenceDto sequenceDto) {
        this.tfFilename.setText(sequenceDto.name);
        this.division.setText("division: " + sequenceDto.division + "");
        this.resolution.setText("resolution: " + sequenceDto.resolution + "");
        this.tickLength.setText("tick length: " + sequenceDto.tickLength + "");
        this.ticksPerMeasure.setText("ticks / measure: " + sequenceDto.ticksPerMeasure + " (4 * resolution)");
        this.ticksIn32nds.setText("ticks in 32nds: " + sequenceDto.ticksIn32nds + " (ticks per measure / 32)");
        this.ticksPerSecond.setText("ticks / sec: " + sequenceDto.ticksPerSecond + " (resolution * (tempo / 60))");
        this.tickSize.setText("tick size: " + sequenceDto.tickSize + " (1 / ticks per second)");
        this.tempoSlider.adjustValue(sequenceDto.tempo);
        this.tempoLabel.setText("Tempo: " + sequenceDto.tempo);
        this.sequenceId = sequenceDto.id;
        sequenceDto.tracks.forEach(trackDto -> addTrackPanel(trackDto, sequenceDto.resolution));
    }

    public void initSequence() {
        this.boundaryIn.create();
    }

    public void initSequence(File file) {
        this.boundaryIn.open(file.getAbsolutePath());
    }

    private void addTrackPanel(TrackDto trackDto, int resolution) {
        FXMLLoader loader = new FXMLLoader(TrackEditor.class.getResource("track-editor.fxml"));
        loader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        TitledPane trackEditor = null;
        try {
            trackEditor = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TrackEditor controller = loader.getController();
        controller.setTrackDto(trackDto, resolution);
        controller.setEventBus(eventBus);

        accordion.getPanes().add(trackEditor);
    }

    public void onNewTrackButtonClicked(ActionEvent actionEvent) {
    }

    public void stopPlayback(ActionEvent actionEvent) {
    }

//    public void onNewTrackButtonClicked(ActionEvent actionEvent) {
//        this.boundaryIn.addTrack(sequenceId);
//    }

//    @Override
//    public void addTrack(int index) {
//        try {
//            addTrackPanel(index);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new MidiAiException("Adding new track failed: " + e.getMessage());
//        }
//    }

//    @Subscribe
//    public void onTrackDeletedEvent(TrackDeleteEvent event) {
//        boundaryIn.removeTrack(sequenceId, event.getIndex());
//    }
//
//    @Subscribe
//    public void onProgramChangedEvent(ProgramChangedEvent even){
//        LOG.debug("programchanged event: " + even);
//        boundaryIn.updateTrackProgram(sequenceId, even.getTrackIndex(), even.getProgram(), even.getChannel());
//    }


}
