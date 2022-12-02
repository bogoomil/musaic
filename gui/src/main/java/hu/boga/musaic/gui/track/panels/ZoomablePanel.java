package hu.boga.musaic.gui.track.panels;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.layout.Pane;

public abstract class ZoomablePanel extends Pane {
    final DoubleProperty zoom, scroll;
    final IntegerProperty resolution, fourthInBar;

    final int measureWidth = 10;
    final int measureNum = 100;

    public ZoomablePanel(DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar) {
        this.zoom = zoom;
        this.scroll = scroll;
        this.resolution = resolution;
        this.fourthInBar = fourthInBar;
        zoom.addListener((observable, oldValue, newValue) -> updateGui());
        scroll.addListener((observable, oldValue, newValue) -> scrollGrid(newValue));
        updateGui();
        scrollGrid(scroll.doubleValue());
    }

    abstract void updateGui();

    private void scrollGrid(Number newValue) {
        double v = getPrefWidth() * (newValue.doubleValue() / 100);
        this.setLayoutX(-1 * v);
    }

    protected int getTickAtX(double x){
        return (int) (x / getTickWith());
    }

    protected double getTickWith(){
        return getFourthWidth() / resolution.intValue();
    }

    protected double getFullWidth(){
        return measureWidth * measureNum * zoom.doubleValue();
    }

    protected double getMeasureWidth(){
        return getFullWidth() / measureNum;
    }

    protected double getFourthWidth(){
        return getMeasureWidth() / fourthInBar.intValue();
    }

}
