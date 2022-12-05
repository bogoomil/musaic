package hu.boga.musaic.gui.track.panels;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.track.events.MeasureSelectedEvent;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectionPanel extends NotesPanelBase{

    private static final Logger LOG = LoggerFactory.getLogger(SelectionPanel.class);

    private final EventBus eventBus;

    private int selectionStartInTicks, selectionEndInTicks;

    public SelectionPanel(DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNum, TrackModell trackModell, EventBus eventBus) {
        super(zoom, scroll, resolution, fourthInBar, measureNum, trackModell);
        this.eventBus = eventBus;
        this.eventBus.register(this);
        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> mouseClicked(event));
    }

    @Override
    void paint() {
        createBorder();
        double x = getXByTick(selectionStartInTicks);
        int width = (int) (getXByTick(selectionEndInTicks) - x);

        Rectangle rectangle = new Rectangle((int) getXByTick(selectionStartInTicks), 0, width, GridPanel.HEIGHT);
        rectangle.setFill(Color.color(Color.DARKORCHID.getRed(), Color.DARKORCHID.getGreen(), Color.DARKORCHID.getBlue(), 0.4));
        getChildren().add(rectangle);
    }

    private void mouseClicked(MouseEvent event) {
        int measureNum = getTickAtX(event.getX()) / (4 * resolution.intValue());
        int selectionStart = measureNum * resolution.intValue() * 4;
        int selectionEnd = selectionStart + (4 * resolution.intValue());

        if(event.getClickCount() == 2){
            LOG.debug("tick2: {}, measure: {}, start tick: {}, end tick: {}", getTickAtX(event.getX()), measureNum, selectionStart, selectionEnd);
        }else if(event.getClickCount() == 1){
            eventBus.post(new MeasureSelectedEvent(selectionStart, selectionEnd));
            LOG.debug("tick1: {}, measure: {}, start tick: {}, end tick: {}", getTickAtX(event.getX()), measureNum, selectionStart, selectionEnd);
        }
    }

    @Subscribe
    void handle(MeasureSelectedEvent event){
        this.selectionStartInTicks = event.getSelectionStart();
        this.selectionEndInTicks = event.getSelectionEnd();
        updateGui();
    }
}
