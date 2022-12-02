package hu.boga.musaic.gui.track.panels;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Group;
import javafx.scene.layout.Pane;

public abstract class ZoomablePanel extends Pane {
    final DoubleProperty zoom, scroll;
    final IntegerProperty resolution, fourthInBar;

    final int measureWidth = 10;
    final int measureNum = 100;

    Group parent;

    public ZoomablePanel(DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar, Group parent) {
        this.zoom = zoom;
        this.scroll = scroll;
        this.resolution = resolution;
        this.fourthInBar = fourthInBar;
        this.parent = parent;
        zoom.addListener((observable, oldValue, newValue) -> updateGui());
        scroll.addListener((observable, oldValue, newValue) -> scrollGrid(newValue));
        parent.getChildren().addAll(this);
        updateGui();
        scrollGrid(scroll.doubleValue());
    }

    abstract void updateGui();

    private void scrollGrid(Number newValue) {
        if(parent != null){
            double v = getPrefWidth() * (newValue.doubleValue() / 100);
            parent.toBack();
            parent.setLayoutX(-1 * v);
        }
    }
}
