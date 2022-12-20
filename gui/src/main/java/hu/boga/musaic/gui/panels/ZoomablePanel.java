package hu.boga.musaic.gui.panels;

import hu.boga.musaic.gui.track.TrackModell;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.layout.Pane;

public abstract class ZoomablePanel extends Pane {
    protected final DoubleProperty zoom;
    protected final IntegerProperty resolution;
    protected final IntegerProperty fourthInBar;
    protected final IntegerProperty measureNum;

    public final static int measureWidth = 10;

    public ZoomablePanel(DoubleProperty zoom, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNumProperty) {
        this.zoom = zoom;
        this.resolution = resolution;
        this.fourthInBar = fourthInBar;
        this.measureNum = measureNumProperty;
        zoom.addListener((observable, oldValue, newValue) -> updateGui());
    }

    protected abstract void updateGui();

    public int getTickAtX(double x) {
        return (int) (x / getTickWith());
    }

    public double getTickWith() {
        return getFourthWidth() / resolution.intValue();
    }

    public double getFullWidth() {
        return measureWidth * measureNum.intValue() * zoom.doubleValue();
    }

    public double getMeasureWidth() {
        return getFullWidth() / measureNum.intValue();
    }

    public double getFourthWidth() {
        return getMeasureWidth() / fourthInBar.intValue();
    }

    public double getXByTick(int tick) {
        return getTickWith() * tick;
    }

    public IntegerProperty getMeasureNum() {
        return measureNum;
    }

    public IntegerProperty measureNumProperty() {
        return measureNum;
    }


    public IntegerProperty fourthInBarProperty() {
        return fourthInBar;
    }
}
