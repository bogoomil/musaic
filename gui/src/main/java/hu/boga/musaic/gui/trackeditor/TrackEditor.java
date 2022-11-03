package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.gui.controls.InstrumentCombo;
import hu.boga.musaic.gui.trackeditor.events.*;
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
import javax.sound.midi.Instrument;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrackEditor implements TrackBoundaryOut, NoteChangeListener {

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
    private int currentChannel;

    ChangeListener<? super Integer> channelComboListener = (observable, oldValue, newValue) -> {
        onProgramChangedEvent(new ProgramChangedEvent(trackDto.id, instrumentCombo.getSelectedProgram(), channelCombo.getSelectionModel().getSelectedIndex()));
    };

    ChangeListener<? super Instrument> instrumentComboListener = (observable, oldValue, newValue) -> {
        if(eventBus != null){
            onProgramChangedEvent(new ProgramChangedEvent(trackDto.id, instrumentCombo.getSelectedProgram(), channelCombo.getSelectionModel().getSelectedIndex()));
        }
    };



    @Inject
    public TrackEditor(TrackBoundaryIn trackBoundaryIn) {
        this.trackBoundaryIn = trackBoundaryIn;
    }

    public void initialize() {
        channelCombo.getItems().addAll(IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList()));

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
        trackEditorPanel.setNoteChangeListener(this);
    }

    public void removeTrack(ActionEvent actionEvent) {
        trackBoundaryIn.removeTrack(trackDto.id);
        this.eventBus.post(new TrackDeletedEvent(trackDto.id));
    }

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Override
    public void setTrackDto(TrackDto trackDto, int resolution) {

        channelCombo.valueProperty().removeListener(channelComboListener);
        instrumentCombo.valueProperty().removeListener(instrumentComboListener);
        this.trackDto = trackDto;

        titledPane.setText("ch: " + trackDto.channel + " pr:" + trackDto.program + " notes: " + trackDto.notes.size() + " (" + trackDto.id + ")");
        channelCombo.getSelectionModel().select(trackDto.channel);
        instrumentCombo.selectInstrument(trackDto.program);
        trackName.setText(trackDto.name);

        trackEditorPanel.setResolution(resolution);
        trackEditorPanel.setNotes(trackDto.notes);
        trackEditorPanel.paintNotes();

        channelCombo.valueProperty().addListener(channelComboListener);
        instrumentCombo.valueProperty().addListener(instrumentComboListener);

    }

    public void onAddChordEvent(AddChordEvent event) {
        trackBoundaryIn.addChord(trackDto.id, event.getTick(), event.getPitch(), event.getLength(), currentChannel, event.getChordType());
    }

//    @Override
//    public void onDeleteNoteEvent(DeleteNoteEvent[] events) {
//        throw new UnsupportedOperationException("DELETE NOTE EVENT");
//
//    }

    @Override
    public void oMoveNoteEvent(MoveNoteEvent... events) {
//        throw new UnsupportedOperationException("MOVE NOTE EVENT");
    }
//
//    @Subscribe
//    public void onMoveNoteEvent(MoveNoteEvent event) {
//        trackBoundaryIn.noteMoved(trackIndex, event.getTick(), event.getPitch(), event.getNewTick());
//    }
//
//    @Subscribe
    public void onDeleteNoteEvent(DeleteNoteEvent... events) {
        List<NoteDto> dtos = Arrays.stream(events).map(event -> convertDeleteEventToNoteDto(event)).collect(Collectors.toList());
        trackBoundaryIn.deleteNotes(trackDto.id, dtos.toArray(NoteDto[]::new));

    }

    NoteDto convertDeleteEventToNoteDto(DeleteNoteEvent event){
        NoteDto dto = new NoteDto();
        dto.midiCode = (int) event.getPitch();
        dto.tick = (long) event.getTick();
        return dto;
    }

    @Subscribe
    private void handleRootChangedEvent(RootChangedEvent event) {
        trackEditorPanel.setCurrentRoot(event.getNoteName());
    }

    @Subscribe
    private void handleModeChangedEvent(ModeChangedEvent event) {
        trackEditorPanel.setCurrentTone(event.getTone());
    }

    public void onProgramChangedEvent(ProgramChangedEvent event){
        LOG.debug("programchanged event: " + event);
        currentChannel = event.getChannel();
        trackBoundaryIn.updateTrackProgram(event.getTrackId(), event.getProgram(), event.getChannel());
    }
}
