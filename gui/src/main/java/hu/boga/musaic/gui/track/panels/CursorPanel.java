package hu.boga.musaic.gui.track.panels;

import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.core.events.EventSystem;
import hu.boga.musaic.core.events.TickEvent;
import hu.boga.musaic.gui.track.TrackModell;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class CursorPanel extends NotesPanelBase{

    private static final Logger LOG = LoggerFactory.getLogger(CursorPanel.class);
    private Line cursor;

    public CursorPanel(int height, DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNum, TrackModell trackModell) {
        super(height, zoom, scroll, resolution, fourthInBar, measureNum);
        EventSystem.EVENT_BUS.register(this);
    }

    @Override
    protected void updateGui() {
        LOG.debug("updating cursor panels gui");
        createBorder();
        initCursor();
    }

    private void initCursor() {
        cursor = new Line(0, 0, 0, height);
        cursor.setStrokeWidth(2);
        cursor.setStroke(Color.RED);
        getChildren().add(cursor);
    }

    @Subscribe
    void handleTickEvent(TickEvent event){
//        cursor.setStartX(getXByTick(event.getTick()));
//        cursor.setEndX(getXByTick(event.getTick()));
    }
}
