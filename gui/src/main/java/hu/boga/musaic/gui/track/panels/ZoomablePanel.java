package hu.boga.musaic.gui.track.panels;

import hu.boga.musaic.gui.track.TrackModell;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.layout.Pane;

public abstract class ZoomablePanel extends Pane {
    final DoubleProperty zoom, scroll;
    final IntegerProperty resolution, fourthInBar;
    protected TrackModell trackModell;
    protected int measureNum;

    protected final static int measureWidth = 10;
    protected final int height;

    public ZoomablePanel(int height, DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNumProperty, TrackModell trackModell) {
        this.zoom = zoom;
        this.scroll = scroll;
        this.resolution = resolution;
        this.fourthInBar = fourthInBar;
        this.trackModell = trackModell;
        this.measureNum = measureNumProperty.get();
        this.height = height;

        zoom.addListener((observable, oldValue, newValue) -> updateGui());
        scroll.addListener((observable, oldValue, newValue) -> scrollGrid(newValue));
        measureNumProperty.addListener((observable, oldValue, newValue) -> {
            measureNum = newValue.intValue();
            updateGui();
        });
        updateGui();
        scrollGrid(scroll.doubleValue());
    }

    abstract void updateGui();

    private void scrollGrid(Number newValue) {
        double v = getPrefWidth() * (newValue.doubleValue() / 100);
        this.setLayoutX(-1 * v);
    }

    protected int getTickAtX(double x) {
        return (int) (x / getTickWith());
    }

    protected double getTickWith() {
        return getFourthWidth() / resolution.intValue();
    }

    protected double getFullWidth() {
        return measureWidth * measureNum * zoom.doubleValue();
    }

    protected double getMeasureWidth() {
        return getFullWidth() / measureNum;
    }

    protected double getFourthWidth() {
        return getMeasureWidth() / fourthInBar.intValue();
    }

    protected double getXByTick(int tick) {
        return getTickWith() * tick;
    }

}
