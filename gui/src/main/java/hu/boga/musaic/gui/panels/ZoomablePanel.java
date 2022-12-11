package hu.boga.musaic.gui.panels;

import hu.boga.musaic.gui.track.TrackModell;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.layout.Pane;

public abstract class ZoomablePanel extends Pane {
    protected final DoubleProperty zoom;
    protected final IntegerProperty resolution, fourthInBar, measureNum;
    protected TrackModell trackModell;

    protected final static int measureWidth = 10;

    public ZoomablePanel(DoubleProperty zoom, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNumProperty, TrackModell trackModell) {
        this.zoom = zoom;
        this.resolution = resolution;
        this.fourthInBar = fourthInBar;
        this.trackModell = trackModell;
        this.measureNum = measureNumProperty;
        zoom.addListener((observable, oldValue, newValue) -> updateGui());
    }

    protected abstract void updateGui();

    protected int getTickAtX(double x) {
        return (int) (x / getTickWith());
    }

    protected double getTickWith() {
        return getFourthWidth() / resolution.intValue();
    }

    protected double getFullWidth() {
        return measureWidth * measureNum.intValue() * zoom.doubleValue();
    }

    protected double getMeasureWidth() {
        return getFullWidth() / measureNum.intValue();
    }

    protected double getFourthWidth() {
        return getMeasureWidth() / fourthInBar.intValue();
    }

    protected double getXByTick(int tick) {
        return getTickWith() * tick;
    }

}
