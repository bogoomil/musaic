package hu.boga.musaic.gui.trackeditor.panels;

import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.gui.panels.ZoomablePanel;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.musictheory.Pitch;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class EditorBasePanel extends ZoomablePanel {

    private static final Logger LOG = LoggerFactory.getLogger(EditorBasePanel.class);

    private final IntegerProperty octaveNum;
    private Optional<Rectangle> border = Optional.empty();

    public EditorBasePanel(DoubleProperty zoom,
                           IntegerProperty resolution,
                           IntegerProperty fourthInBar,
                           IntegerProperty measureNumProperty,
                           TrackModell trackModell,
                           IntegerProperty octaveNum) {
        super(zoom, resolution, fourthInBar, measureNumProperty, trackModell);
        this.octaveNum = octaveNum;
        this.octaveNum.addListener((observable, oldValue, newValue) -> updateGui());

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            LOG.debug("tick: {}, pitch: {}", getTickAtX(ev.getX()), getPitchByY(ev.getY()));
        });
    }

    @Override
    protected void updateGui() {
        createBorderRetangle(getFullWidth(), getFullHeight());
    }

    protected double getFullHeight() {
        return GuiConstants.NOTE_LINE_HEIGHT * octaveNum.intValue() * 12;
    }

    private void createBorderRetangle(double width, double height) {
        border.ifPresentOrElse(rectangle -> {
            rectangle.setWidth(width);
            rectangle.setHeight(height);
        }, () -> {
            initBorder(width, height);
        });
    }

    private void initBorder(double width, double height) {
        Rectangle rectangle = new Rectangle(0, 0, width, height);
        getChildren().add(rectangle);
        rectangle.setFill(null);
        rectangle.setStroke(Color.DARKORCHID);
        rectangle.setStrokeWidth(2);
        border = Optional.of(rectangle);
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

}
