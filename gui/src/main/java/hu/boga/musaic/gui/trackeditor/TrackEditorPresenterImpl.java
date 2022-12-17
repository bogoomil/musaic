package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import hu.boga.musaic.gui.controls.ChordTypeCombo;
import hu.boga.musaic.gui.controls.ModeCombo;
import hu.boga.musaic.gui.controls.NoteLengthCombo;
import hu.boga.musaic.gui.controls.NoteNameCombo;
import hu.boga.musaic.gui.logic.Observable;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.track.TrackService;
import hu.boga.musaic.gui.trackeditor.layered.LayeredPane;
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

    private final TrackService service;
    private final EventBus eventBus;
    private final IntegerProperty resolution, fourthInBar, measureNum, currentMeasure;

    private final Observable<NoteLength> noteLengthProperty;
    private final Observable<ChordType> chordtTypeProperty;

    private final Observable<TrackModell> trackModellObservable;
    private LayeredPane layeredPane;

    @AssistedInject
    public TrackEditorPresenterImpl(TrackService service, @Assisted Observable<TrackModell> observable,
                                    @Assisted("resolution") IntegerProperty resolution,
                                    @Assisted("fourthInBar") IntegerProperty fourthInBar,
                                    @Assisted("measureNum") IntegerProperty measureNum,
                                    @Assisted("currentMeasure") IntegerProperty currentMeasure,
                                    @Assisted("eventBus") EventBus eventBus) {
        this.service = service;
        this.eventBus = eventBus;
        this.resolution = resolution;
        this.fourthInBar = fourthInBar;
        this.measureNum = measureNum;
        this.currentMeasure = currentMeasure;
        noteLengthProperty = new Observable<>("noteLength");
        chordtTypeProperty = new Observable<>("chordType");
        chordtTypeProperty.setValue(ChordType.NONE);
        noteLengthProperty.setValue(NoteLength.HARMICKETTED);
        this.trackModellObservable = observable;
    }

    public void initialize(){
        zoomSlider.setMin(1);
        zoomSlider.setValue(10);
        noteLength.setValue(NoteLength.HARMICKETTED);
        noteLength.addEventHandler(ActionEvent.ACTION, event -> noteLengthProperty.setValue(noteLength.getSelectedNoteLength()));
        chordType.setValue(ChordType.NONE);
        chordType.addEventHandler(ActionEvent.ACTION, event -> chordtTypeProperty.setValue(chordType.getSelectedChordType()));
        layeredPane = new LayeredPane(zoomSlider.valueProperty(), resolution, fourthInBar, measureNum, new SimpleIntegerProperty(10), noteLengthProperty, chordtTypeProperty);
        panelGroup.getChildren().add(layeredPane);
        service.load(trackModellObservable.getName());
    }
}
