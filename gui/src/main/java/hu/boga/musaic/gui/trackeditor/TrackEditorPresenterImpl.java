package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import hu.boga.musaic.gui.controls.ChordTypeCombo;
import hu.boga.musaic.gui.controls.ModeCombo;
import hu.boga.musaic.gui.controls.NoteLengthCombo;
import hu.boga.musaic.gui.controls.NoteNameCombo;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.trackeditor.panels.*;
import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteLength;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackEditorPresenterImpl implements TrackEditorPresenter{
    private static final Logger LOG = LoggerFactory.getLogger(TrackEditorPresenterImpl.class);
    @FXML
    private NoteLengthCombo noteLength;
    @FXML
    private ChordTypeCombo chordType;
    @FXML
    private Group panelGroup;
    @FXML
    private Button btnDuplicateLoop;
    @FXML
    private ModeCombo modeCombo;
    @FXML
    private NoteNameCombo rootCombo;
    @FXML
    private Button btnClearMode;
    @FXML
    private Slider zoomSlider;
    @FXML
    private Label zoomLabel;
    @FXML
    private Button btnMoveUp;
    @FXML
    private Button btnMoveDown;

    private final TrackEditorService service;
    private final String trackId;
    private final EventBus eventBus;
    private final IntegerProperty resolution, fourthInBar, measureNum, currentMeasure;

    private final MyObservable<NoteLength> noteLengthProperty;
    private final MyObservable<ChordType> chordtTypeProperty;

    private TrackModell trackModell;

    @AssistedInject
    public TrackEditorPresenterImpl(TrackEditorService service, @Assisted String trackId,
                                    @Assisted("resolution") IntegerProperty resolution,
                                    @Assisted("fourthInBar") IntegerProperty fourthInBar,
                                    @Assisted("measureNum") IntegerProperty measureNum,
                                    @Assisted("currentMeasure") IntegerProperty currentMeasure,
                                    @Assisted("eventBus") EventBus eventBus) {
        this.service = service;
        this.trackId = trackId;
        this.eventBus = eventBus;
        this.resolution = resolution;
        this.fourthInBar = fourthInBar;
        this.measureNum = measureNum;
        this.currentMeasure = currentMeasure;

        noteLengthProperty = new MyObservable<>("noteLength");
        chordtTypeProperty = new MyObservable<>("chordType");

        chordtTypeProperty.setValue(ChordType.NONE);
        noteLengthProperty.setValue(NoteLength.HARMICKETTED);

        LOG.debug("service: {}, modell: {}, measure: {}", service, trackId, measureNum.intValue());
    }

    public void initialize(){
        zoomSlider.setMin(1);
        zoomSlider.setValue(10);
        service.load(trackId);

        noteLength.setValue(NoteLength.HARMICKETTED);
        noteLength.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                noteLengthProperty.setValue(noteLength.getSelectedNoteLength());
            }
        });
        chordType.setValue(ChordType.NONE);
        chordType.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                chordtTypeProperty.setValue(chordType.getSelectedChordType());
            }
        });
        updateGui();

    }

    @Override
    public void updateGui() {
        this.trackModell = service.getModell();
        initPanels();
    }

    private void initPanels() {
        panelGroup.getChildren().add(new GridPanel(zoomSlider.valueProperty(), resolution, fourthInBar, measureNum, trackModell, new SimpleIntegerProperty(10)));
//        panelGroup.getChildren().add(new NotesLayer(zoomSlider.valueProperty(), resolution, fourthInBar, measureNum, trackModell, new SimpleIntegerProperty(10), eventBus));
        panelGroup.getChildren().add(new NotesLayer(zoomSlider.valueProperty(), resolution, fourthInBar, measureNum, trackModell, new SimpleIntegerProperty(10), noteLengthProperty, chordtTypeProperty, eventBus));
    }
}
