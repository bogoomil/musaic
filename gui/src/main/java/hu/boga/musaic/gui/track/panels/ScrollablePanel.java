package hu.boga.musaic.gui.track.panels;

import hu.boga.musaic.gui.panels.ZoomablePanel;
import hu.boga.musaic.gui.track.TrackModell;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;

public abstract class ScrollablePanel extends ZoomablePanel {
    final DoubleProperty scroll;
    protected final int height;


    public ScrollablePanel(int height, DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNumProperty, TrackModell trackModell) {
        super(zoom, resolution, fourthInBar, measureNumProperty, trackModell);
        this.height = height;
        this.scroll = scroll;
        scroll.addListener((observable, oldValue, newValue) -> scrollGrid(newValue));
        measureNumProperty.addListener((observable, oldValue, newValue) -> {
            measureNum = newValue.intValue();
            updateGui();
        });
        updateGui();
        scrollGrid(scroll.doubleValue());
    }

    protected abstract void updateGui();

    private void scrollGrid(Number newValue) {
        double v = getPrefWidth() * (newValue.doubleValue() / 100);
        this.setLayoutX(-1 * v);
    }
}
