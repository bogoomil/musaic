package hu.boga.musaic.gui.trackeditor.layered;

import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.core.events.EventSystem;
import hu.boga.musaic.core.events.TickEvent;
import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.musictheory.enums.NoteName;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GridLayer extends Group implements Layer {

    LayeredPane parent;

    private Rectangle progressBar;

    public GridLayer(LayeredPane parent) {
        this.parent = parent;
    }

    @Override
    public void updateGui() {
        getChildren().clear();
        createMeasures();
        createVerticalLines();
        createHorizontalLines();
        createTexts();
        initProgressBar();
    }

    private void initProgressBar() {
        Color highLight = Color.GRAY;
        highLight = Color.color(highLight.getRed(), highLight.getGreen(), highLight.getBlue(), 0.4);

        progressBar = new Rectangle(0, 0, 0, parent.getFullHeight());
        progressBar.setFill(highLight);
        getChildren().add(progressBar);
    }

    private void createTexts() {
        final List<String> noteNames = Arrays.stream(NoteName.values())
                .sorted(Comparator.comparingInt(NoteName::ordinal).reversed())
                .map(noteName -> noteName.name()).collect(Collectors.toList());
        final int increment = GuiConstants.NOTE_LINE_HEIGHT;
        int y = increment;
        for (int i = 0; i < parent.octaveNum.intValue(); i++) {
            for (int j = 0; j < 12; j++) {
                final Text text = new Text(noteNames.get(j) + " " + (parent.octaveNum.intValue() - i));
                text.setX(5);
                text.setY(y - 3);
                text.setStroke(Color.WHITE);
                text.setFont(Font.font("arial", FontWeight.LIGHT, 8));
                y += increment;
                getChildren().add(text);
            }
        }
    }

    private void createHorizontalLines() {
        double width = parent.getFullWidth();
        double height = parent.getFullHeight();
        for (int y = 0; y < height; y += GuiConstants.NOTE_LINE_HEIGHT) {
            createHorizontalLine(width, y);
        }
    }

    private void createHorizontalLine(double width, int y) {
        final Line line = new Line();
        line.setStrokeWidth(0.5);
        line.setStroke(Color.RED);
        line.setStartX(0);
        line.setStartY(y);
        line.setEndX(width);
        line.setEndY(y);
        getChildren().add(line);
    }

    private void createVerticalLines() {
        int countOf32ndsInBar = parent.get32ndsCountInBar();
        int countOf32nds = countOf32ndsInBar * parent.getMeasureNum().intValue();
        final double w32nds = parent.get32ndsWidth();
        double x = 0;
        for (int i = 0; i < countOf32nds; i++) {
            createVerticalLine(countOf32ndsInBar, x, i);
            x += w32nds;
        }
    }

    private void createVerticalLine(int countOf32ndsInBar, double x, int i) {
        final Line line = new Line();
        line.setStartX(x);
        line.setStartY(0);
        line.setEndX(x);
        line.setEndY(parent.getFullHeight());
        if (i % countOf32ndsInBar == 0) {
            line.setStrokeWidth(1);
            line.setStroke(Color.BLACK);
        } else {
            line.setStrokeWidth(0.5);
            line.setStroke(Color.RED);
        }
        getChildren().addAll(line);
    }

    private void createMeasures() {
        for (int i = 0; i < parent.getMeasureNum().intValue(); i++) {
            createMeasure(i);
        }
    }

    private void createMeasure(int i) {
        double height = parent.getFullHeight();
        double barW = parent.getMeasureWidth();
        double rectW = parent.getFourthWidth();
        for (int j = 0; j < parent.fourthInBarProperty().intValue(); j++) {
            double rectX = i * barW + j * rectW;
            Rectangle rectangle = new Rectangle(rectX, 0, rectW, height);
            rectangle.setFill(j % 2 == 0 ? Color.LIGHTSKYBLUE : Color.DEEPSKYBLUE);
            getChildren().add(rectangle);
        }
    }

    @Subscribe
    void handleTickEvent(TickEvent event){
        Platform.runLater(() -> {
            progressBar.setWidth(parent.getXByTick(event.getTick()));
        });
    }


}
