package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.gui.controls.InstrumentCombo;
import hu.boga.musaic.gui.trackeditor.events.ModeChangedEvent;
import hu.boga.musaic.gui.trackeditor.events.ProgramChangedEvent;
import hu.boga.musaic.gui.trackeditor.events.RootChangedEvent;
import hu.boga.musaic.gui.trackeditor.events.TrackDeletedEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrackEditor implements TrackBoundaryOut {

    private static final Logger LOG = LoggerFactory.getLogger(TrackEditor.class);

    @FXML
    public InstrumentCombo instrumentCombo;
    @FXML
    public TitledPane titledPane;
    @FXML
    public TrackEditorPanel trackEditorPanel;
    @FXML
    public Slider zoomSlider;
    @FXML
    public TextField trackName;
    @FXML
    public Label zoomLabel;
    @FXML
    ComboBox<Integer> channelCombo;

    TrackBoundaryIn trackBoundaryIn;

    private EventBus eventBus;
    private TrackDto trackDto;

    @Inject
    public TrackEditor(TrackBoundaryIn trackBoundaryIn) {
        this.trackBoundaryIn = trackBoundaryIn;
    }

    public void initialize() {
        channelCombo.getItems().addAll(IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList()));

        channelCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(eventBus != null){
                eventBus.post(new ProgramChangedEvent(trackDto.id, instrumentCombo.getSelectedProgram(), channelCombo.getSelectionModel().getSelectedIndex()));
            }
        });

        instrumentCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(eventBus != null){
                eventBus.post(new ProgramChangedEvent(trackDto.id, instrumentCombo.getSelectedProgram(), channelCombo.getSelectionModel().getSelectedIndex()));
            }
        });

        zoomSlider.setMin(10);
        zoomSlider.setMax(400);
        zoomSlider.adjustValue(100);
        zoomLabel.setText("Zoom: 100%");
        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                float zoomFactor = newValue.floatValue() / 100;
                trackEditorPanel.setZoomFactor(zoomFactor);
                trackEditorPanel.paintNotes();
                zoomLabel.setText("Zoom: " + newValue.intValue() + "%");

            }
        });
        trackName.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                trackDto.name = trackName.getText();
                trackBoundaryIn.updateTrackName(trackDto);
            }
        });
    }

    public void removeTrack(ActionEvent actionEvent) {
        trackBoundaryIn.removeTrack(trackDto.id);
        this.eventBus.post(new TrackDeletedEvent(trackDto.id));
    }

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
        trackEditorPanel.setEventBus(eventBus);
    }

    public void setTrackDto(TrackDto trackDto, int resolution) {

        this.trackDto = trackDto;

        titledPane.setText("ch: " + trackDto.channel + " pr:" + trackDto.program + " notes: " + trackDto.notes.size() + " (" + trackDto.id + ")");
        channelCombo.getSelectionModel().select(trackDto.channel);
        instrumentCombo.selectInstrument(trackDto.program);
        trackName.setText(trackDto.name);

        trackEditorPanel.setResolution(resolution);
        trackEditorPanel.setNotes(trackDto.notes);
        trackEditorPanel.paintNotes();

    }


//    @Subscribe
//    public void onAddNoteEvent(AddNoteEvent event) {
//        trackBoundaryIn.addNote(trackIndex, event.getTick(), event.getPitch(), event.getLength());
//    }
//
//    @Subscribe
//    public void onAddChordEvent(AddChordEvent event) {
//        trackBoundaryIn.addChord(trackIndex, event.getTick(), event.getPitch(), event.getLength(), event.getChordType());
//    }
//
//    @Subscribe
//    public void onMoveNoteEvent(MoveNoteEvent event) {
//        trackBoundaryIn.noteMoved(trackIndex, event.getTick(), event.getPitch(), event.getNewTick());
//    }
//
//    @Subscribe
//    public void onDeleteNoteEvent(DeleteNoteEvent... events) {
//        List<NoteDto> dtos = Arrays.stream(events).map(event -> new NoteDto(event.getPitch(), event.getTick(), 0)).collect(Collectors.toList());
//        trackBoundaryIn.deleteNote(trackIndex, dtos.toArray(NoteDto[]::new));
//
//    }

    @Subscribe
    private void handleRootChangedEvent(RootChangedEvent event) {
        trackEditorPanel.setCurrentRoot(event.getNoteName());
    }

    @Subscribe
    private void handleModeChangedEvent(ModeChangedEvent event) {
        trackEditorPanel.setCurrentTone(event.getTone());
    }

}
