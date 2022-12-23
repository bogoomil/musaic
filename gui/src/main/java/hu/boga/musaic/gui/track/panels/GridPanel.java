package hu.boga.musaic.gui.track.panels;

import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.core.events.EventSystem;
import hu.boga.musaic.core.events.TickEvent;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GridPanel extends ScrollablePanel {

    private static final Logger LOG = LoggerFactory.getLogger(GridPanel.class);

    private Rectangle progressBar;

    public GridPanel(int height, DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNum) {
        super(height, zoom, scroll, resolution, fourthInBar, measureNum);
        EventSystem.EVENT_BUS.register(this);
    }

    @Override
    protected void updateGui() {
        paintGrid();
    }

    private void paintGrid() {
        getChildren().clear();
        setPrefWidth(measureWidth * measureNum.intValue() * zoom.doubleValue());
        int x = 0;
        progressBar = new Rectangle(0,0,0,5);
        for(int i = 0; i < measureNum.intValue(); i++){
            createMeasure(i);
        }
        getChildren().add(progressBar);
    }

    private void createMeasure(int i) {
        double measureEndX = 0;
        double barW = measureWidth * zoom.doubleValue();
        double rectW = barW / fourthInBar.intValue();
        for(int j = 0; j < fourthInBar.intValue(); j++){
            double rectX = i * barW + j * rectW;
            measureEndX = rectW + rectX;
            Rectangle rectangle = new Rectangle(rectX , 0, rectW, height);
            rectangle.setFill(j % 2 == 0 ? Color.LIGHTSKYBLUE : Color.DEEPSKYBLUE);
            getChildren().add(rectangle);
        }

        Line line = new Line(measureEndX, 0, measureEndX, height);
        line.setStroke(Color.RED);
        line.setStrokeWidth(1);
        getChildren().add(line);
    }

    @Subscribe
    void handleTickEvent(TickEvent event){
        Platform.runLater(() -> {
            progressBar.setWidth(getXByTick(event.getTick()));
        });
    }
}
