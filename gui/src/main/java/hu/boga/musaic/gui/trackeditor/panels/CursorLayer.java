package hu.boga.musaic.gui.trackeditor.panels;

import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteLength;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.input.MouseEvent;

import java.beans.PropertyChangeEvent;

public class CursorLayer extends EditorBasePanel {
    private Cursor cursor = new Cursor();

    public CursorLayer(DoubleProperty zoom,
                       IntegerProperty resolution,
                       IntegerProperty fourthInBar,
                       IntegerProperty measureNumProperty,
                       IntegerProperty octaveNum,
                       Observable<NoteLength> noteLengthObservable,
                       Observable<ChordType> chordTypeObservable) {
        super(zoom, resolution, fourthInBar, measureNumProperty, octaveNum);
        this.getChildren().add(cursor);
        noteLengthObservable.addPropertyChangeListener(this::noteLengthPropertyChange);
        chordTypeObservable.addPropertyChangeListener(this::chordTypePropertyChange);
        zoom.addListener((observable, oldValue, newValue) -> {
            cursor.setWidth(calculateWidth(noteLengthObservable.value));
        });
        cursor.setChordType(ChordType.NONE);
        cursor.setWidth(calculateWidth(NoteLength.HARMICKETTED));
    }

    private double calculateWidth(NoteLength noteLength) {
        return noteLength.getErtek() * get32ndsWidth();
    }

    @Override
    protected void updateGui() {
        super.updateGui();
        this.setOnMouseEntered(event -> showCursor(event));
        this.setOnMouseMoved(event -> moveCursor(event));
        this.setOnMouseExited(event -> hideCursor());
    }

    private void hideCursor() {
        cursor.setVisible(false);
    }

    private void moveCursor(MouseEvent event) {
        cursor.setLayoutX(getCaculatedX(event.getX()));
        int y = getYByPitch(getPitchByY((int) event.getY()).getMidiCode());
        cursor.setLayoutY(y);
    }

    protected double getCaculatedX(double x) {
        return (x - (x % get32ndsWidth()));
    }

    private void showCursor(MouseEvent event) {
        cursor.setLayoutX(getCaculatedX(event.getX()));
        cursor.setLayoutY(event.getY() - cursor.getHeight());
        cursor.setVisible(true);
    }

    private void chordTypePropertyChange(PropertyChangeEvent event) {
        cursor.setChordType((ChordType) event.getNewValue());
    }

    private void noteLengthPropertyChange(PropertyChangeEvent event) {
        cursor.setWidth(calculateWidth((NoteLength) event.getNewValue()));
    }
}
