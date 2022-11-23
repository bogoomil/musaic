package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.core.note.NoteBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.gui.controls.ModeCombo;
import hu.boga.musaic.gui.controls.NoteNameCombo;
import hu.boga.musaic.gui.trackeditor.events.AddChordEvent;
import hu.boga.musaic.gui.trackeditor.events.DeleteNoteEvent;
import hu.boga.musaic.gui.trackeditor.events.NoteMovedEvent;
import hu.boga.musaic.gui.trackeditor.events.NotePlayEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrackEditor implements TrackBoundaryOut, NoteChangeListener {

    @FXML
    public TrackEditorPanel trackEditorPanel;

    public Slider zoomSlider;
    public Label zoomLabel;
    public BorderPane borderPane;
    public NoteNameCombo rootCombo;
    public ModeCombo modeCombo;
    public Button btnClearMode;
    public Label xLabel;
    public AnchorPane topAnchorPane;
    public Button btnDuplicateLoop;
    public Button btnMoveUp;
    public Button btnMoveDown;

    private TrackBoundaryIn trackBoundaryIn;
    private NoteBoundaryIn noteBoundaryIn;

    private TrackDto trackDto;
    private int resolution;
    private EventBus eventBus;

    @Inject
    public TrackEditor(TrackBoundaryIn trackBoundaryIn, NoteBoundaryIn noteBoundaryIn) {
        this.trackBoundaryIn = trackBoundaryIn;
        this.noteBoundaryIn = noteBoundaryIn;
    }

    public void initialize() {
        zoomSlider.setMin(10);
        zoomSlider.setMax(400);
        zoomSlider.adjustValue(100);
        zoomLabel.setText("Zoom: 100%");
        zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            float zoomFactor = newValue.floatValue() / 100;
            trackEditorPanel.setZoomFactor(zoomFactor);
            trackEditorPanel.paintNotes();
            zoomLabel.setText("Zoom: " + newValue.intValue() + "%");

        });
        trackEditorPanel.setNoteChangeListener(this);
        trackEditorPanel.setNoteBoundaryIn(noteBoundaryIn);
        rootCombo.addEventHandler(ActionEvent.ACTION, event -> trackEditorPanel.setCurrentRoot(rootCombo.getSelectedNoteName()));
        modeCombo.addEventHandler(ActionEvent.ACTION, event -> trackEditorPanel.setCurrentTone(modeCombo.getSelectedTone()));

        btnClearMode.setOnAction(event -> {
            modeCombo.getSelectionModel().clearSelection();
            rootCombo.getSelectionModel().clearSelection();
            trackEditorPanel.setCurrentRoot(null);
            trackEditorPanel.setCurrentTone(null);
        });

        btnMoveUp.setOnAction(event -> {
            moveSelectedNotes(1);
        });
        btnMoveDown.setOnAction(event -> {
            moveSelectedNotes(-1);
        });
    }

    private void moveSelectedNotes(int i) {
        List<String> ids = trackEditorPanel.getSelectedNoteRectangles().stream()
                .map(noteRectangle -> noteRectangle.getNoteId())
                .collect(Collectors.toList());
        trackBoundaryIn.moveUpAndDownNotes(trackDto.id, ids.toArray(new String[0]), i);
    }

    public void setTrack(String trackId, String currentColor){
        try{
            setTopPaneColor(Color.web(currentColor));
        }catch (Exception e){
            setTopPaneColor(Color.GRAY);
        }

        trackBoundaryIn.showTrack(trackId);
    }

    private void setTopPaneColor(Color color) {
        trackEditorPanel.setNoteColor(color);
        topAnchorPane.setBackground(new Background(
                new BackgroundFill(
                        color,
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
    }

    @Override
    public void displayTrack(TrackDto trackDto) {
        this.trackDto = trackDto;
        trackEditorPanel.setResolution(resolution);
        trackEditorPanel.setNotes(trackDto.notes);
        trackEditorPanel.paintNotes();

        eventBus.post(new TrackChangedEvent());
    }

    @Override
    public void onAddChordEvent(AddChordEvent event) {
        trackBoundaryIn.addChord(trackDto.id, event.getTick(), event.getPitch(), event.getLength(), event.getChordType());
    }

    @Override
    public void onDeleteNoteEvent(DeleteNoteEvent... events) {
        List<NoteDto> dtos = Arrays.stream(events).map(event -> convertDeleteEventToNoteDto(event)).collect(Collectors.toList());
        trackBoundaryIn.deleteNotes(trackDto.id, dtos.toArray(NoteDto[]::new));
    }

    @Override
    public void onNoteMoved(NoteMovedEvent... events) {
        Arrays.stream(events).forEach(event -> {
            trackBoundaryIn.moveNote(event.getId(), event.getNewTick());
        });
        trackBoundaryIn.showTrack(trackDto.id);
    }

    @Override
    public void onNotePlay(NotePlayEvent event) {
        this.noteBoundaryIn.play(trackDto.id, event.midiCode, event.lengthInTicks);
    }


    private NoteDto convertDeleteEventToNoteDto(DeleteNoteEvent event){
        NoteDto dto = new NoteDto();
        dto.id = event.getNoteId();
        return dto;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public int getLoopStart(){
        return trackEditorPanel.getLoopStartTick();
    }

    public int getLoopEnd(){
        return trackEditorPanel.getLoopEndTick();
    }


    public void duplicate(ActionEvent actionEvent) {
        this.trackBoundaryIn.duplicate(trackDto.id, getLoopStart(), getLoopEnd());
        int newLoopEnd = getLoopEnd() + (getLoopEnd() - getLoopStart());
        this.trackEditorPanel.setLoopEnd(newLoopEnd);
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this.trackEditorPanel);
    }

    public static class TrackChangedEvent {
    }
}
