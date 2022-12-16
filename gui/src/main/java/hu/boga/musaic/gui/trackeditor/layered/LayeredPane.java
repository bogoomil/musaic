package hu.boga.musaic.gui.trackeditor.layered;

import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.gui.panels.ZoomablePanel;
import hu.boga.musaic.gui.logic.Observable;
import hu.boga.musaic.musictheory.Pitch;
import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteLength;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class LayeredPane extends ZoomablePanel {
    public final IntegerProperty octaveNum;
    private final Rectangle border = new Rectangle();
    private final List<Layer> layers = new ArrayList<>();
    private GridLayer gridLayer;
    private CursorLayer cursor;

    public LayeredPane(DoubleProperty zoom,
                       IntegerProperty resolution,
                       IntegerProperty fourthInBar,
                       IntegerProperty measureNumProperty,
                       IntegerProperty octaveNum,
                       Observable<NoteLength> noteLengthObservable,
                       Observable<ChordType> chordTypeObservable) {
        super(zoom, resolution, fourthInBar, measureNumProperty);
        this.octaveNum = octaveNum;
        this.getChildren().add(border);

        cursor = new CursorLayer(this, noteLengthObservable, chordTypeObservable, zoom);
        updateGui();
        initLayers();
        initCursor();
    }

    private void initCursor() {
        layers.add(cursor);
        this.getChildren().add(cursor);
        this.addEventHandler(MouseEvent.ANY, cursor);
    }

    private void initLayers() {
        gridLayer = new GridLayer(this);
        layers.add(gridLayer);
        this.getChildren().add(gridLayer);
        gridLayer.updateGui();
    }

    @Override
    protected void updateGui() {
        this.layers.forEach(layer -> layer.updateGui());
    }

    protected double getFullHeight() {
        return GuiConstants.NOTE_LINE_HEIGHT * octaveNum.intValue() * 12;
    }

    protected Pitch getPitchByY(final double y) {
        final int pitch = (int) ((getFullHeight() / GuiConstants.NOTE_LINE_HEIGHT) - (y / GuiConstants.NOTE_LINE_HEIGHT));
        return new Pitch(pitch);
    }

    protected double get32ndsWidth() {
        return measureWidth * this.zoom.doubleValue() / get32ndsCountInBar();
    }

    protected int get32ndsCountInBar(){
        return fourthInBar.intValue() * 8;
    }

    protected int getYByPitch(final int midiCode) {
        return (octaveNum.intValue() * 12 - 1 - midiCode) * GuiConstants.NOTE_LINE_HEIGHT;
    }

    protected double getTickWidth() {
        return measureWidth * zoom.doubleValue() / (this.resolution.intValue() * fourthInBar.intValue());
    }

}
