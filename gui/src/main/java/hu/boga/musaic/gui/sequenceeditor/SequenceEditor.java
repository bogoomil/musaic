package hu.boga.musaic.gui.sequenceeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.GuiceModule;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.gui.controls.InstrumentCombo;
import hu.boga.musaic.gui.controls.TempoSlider;
import hu.boga.musaic.gui.trackeditor.TrackEditor;
import hu.boga.musaic.gui.trackeditor.TrackProperties;
import hu.boga.musaic.gui.trackeditor.events.TrackDeletedEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class SequenceEditor implements SequenceBoundaryOut {

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

    public final EventBus eventBus;
    public VBox propertiesVBox;
    public HBox centerPane;
    public VBox tracksVBox;

    private SequenceDto sequenceDto;
    private TrackEditor trackEditor;
    private long fromTick = 0;
    private long toTick = 512;
    private boolean isPlaying;


    @Inject
    public SequenceEditor(SequenceBoundaryIn boundaryInProvider, EventBus eventBus) {
        this.boundaryIn = boundaryInProvider;
        this.eventBus = eventBus;
        eventBus.register(this);

    }

    public void initialize() {
        tempoSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    initTemposSettings(newValue);
                }
        );
        initTrackEditor();
    }

    private void initTrackEditor() {
        FXMLLoader loader = new FXMLLoader(TrackEditor.class.getResource("track-editor.fxml"));
        loader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        BorderPane borderPane = null;
        try {
            borderPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        trackEditor = loader.getController();
        trackEditor.setEventBus(eventBus);
        centerPane.getChildren().add(borderPane);
    }

    private void initTrackPropertiesPanel(TrackDto trackDto) {
        FXMLLoader loader = new FXMLLoader(TrackEditor.class.getResource("track-properties.fxml"));
        loader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        AnchorPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TrackProperties trackProperties = loader.getController();
        trackProperties.setTrackDto(trackDto, trackEditor, sequenceDto.channelToColorMappings);
        trackProperties.setEventBus(eventBus);
        tracksVBox.getChildren().add(pane);
    }

    private void initTemposSettings(Number newValue) {
        tempoLabel.setText("Tempo: " + newValue.intValue());
        if (sequenceDto.id != null) {
            boundaryIn.setTempo(sequenceDto.id, newValue.intValue());
        }
    }

    public void saveSequence(ActionEvent actionEvent) {
        LOG.debug("Saving sequence: " + sequenceDto.id);
        String path = new FileChooser().showSaveDialog(null).getAbsolutePath();
        this.boundaryIn.save(sequenceDto.id, path);
    }

    public void onPlayCurrentSec(ActionEvent actionEvent) {
        isPlaying = true;
        boundaryIn.play(sequenceDto.id, trackEditor.getLoopStart(), trackEditor.getLoopEnd());
    }

    public void stopPlayback(ActionEvent actionEvent) {
        isPlaying = false;
        this.boundaryIn.stop();
    }

    @Override
    public void displaySequence(SequenceDto sequenceDto) {
        this.sequenceDto = sequenceDto;
        trackEditor.setResolution(sequenceDto.resolution);
        updateGui(sequenceDto);
    }

    private void updateGui(SequenceDto sequenceDto) {
        this.division.setText("[division: " + sequenceDto.division + "] ");
        this.resolution.setText("[resolution: " + sequenceDto.resolution + "] ");
        this.tickLength.setText("[tick length: " + sequenceDto.tickLength + "] ");
        this.ticksPerMeasure.setText("[ticks / measure: " + sequenceDto.ticksPerMeasure + " (4 * resolution)] ");
        this.ticksIn32nds.setText("[ticks in 32nds: " + sequenceDto.ticksIn32nds + " (ticks per measure / 32)] ");
        this.ticksPerSecond.setText("[ticks / sec: " + sequenceDto.ticksPerSecond + " (resolution * (tempo / 60))] ");
        this.tickSize.setText("[tick size: " + sequenceDto.tickSize + " (1 / ticks per second)] ");
        this.tempoSlider.adjustValue(sequenceDto.tempo);
        this.tempoLabel.setText("Tempo: " + sequenceDto.tempo);

        this.propertiesVBox.getChildren().clear();
        this.tracksVBox.getChildren().clear();

        createChannelMappingPanel();

        sequenceDto.tracks.forEach(trackDto -> displayNewTrack(trackDto));
    }

    private void createChannelMappingPanel() {
        for (int i = 0; i < 16; i++) {
            HBox hBox = new HBox();

            Label l = new Label("" + i);
            hBox.getChildren().add(l);

            InstrumentCombo instrCombo = new InstrumentCombo();
            instrCombo.selectInstrument(sequenceDto.channelToProgramMappings[i]);

            int finalI = i;
            instrCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
                boundaryIn.updateChannelToProgramMappings(sequenceDto.id, finalI, instrCombo.getSelectedProgram());
            });
            hBox.getChildren().add(instrCombo);
            ColorPicker colorPicker = new ColorPicker();
            if (sequenceDto.channelToColorMappings[i] != null) {
                instrCombo.setBackground(new Background(
                        new BackgroundFill(
                                Color.web(sequenceDto.channelToColorMappings[i]),
                                CornerRadii.EMPTY,
                                Insets.EMPTY)));
                colorPicker.setValue(Color.web(sequenceDto.channelToColorMappings[i]));
            }
            hBox.getChildren().add(colorPicker);
            colorPicker.setOnAction(event -> {
                boundaryIn.updateChannelColorMapping(sequenceDto.id, finalI, colorPicker.getValue().toString());
            });
            propertiesVBox.getChildren().add(hBox);
        }
    }

    public void initSequence() {
        this.boundaryIn.create();
    }

    public void initSequence(File file) {
        this.boundaryIn.open(file.getAbsolutePath());
    }

//    @Override
    public void displayNewTrack(TrackDto trackDto) {
        trackEditor.setTrack(trackDto.id, null);
        initTrackPropertiesPanel(trackDto);
    }

    public void onNewTrackButtonClicked(ActionEvent actionEvent) {
        this.boundaryIn.addTrack(sequenceDto.id);
    }

    @Subscribe
    public void onTrackDeletedEvent(TrackDeletedEvent event) {
        boundaryIn.reloadSequence(sequenceDto.id);
    }

    @Subscribe
    private void onTrackChangedEvent(TrackEditor.TrackChangedEvent event){
        if(isPlaying){
            boundaryIn.stop();
            boundaryIn.play(sequenceDto.id, trackEditor.getLoopStart(), trackEditor.getLoopEnd());
        }
    }

    @Subscribe
    private void onDuplicateTrack(TrackProperties.DuplicateTrackEvent event) {
        boundaryIn.duplicateTrack(event.getTrackId());
    }
}
