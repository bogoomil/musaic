package hu.boga.musaic.gui.trackeditor.panels;

import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.trackeditor_old.TrackEditorPanel;
import hu.boga.musaic.musictheory.enums.NoteName;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class GridPanel extends EditorBasePanel {

    private static final Logger LOG = LoggerFactory.getLogger(GridPanel.class);

    Group shapes = new Group();

    public GridPanel(DoubleProperty zoom, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNumProperty, TrackModell trackModell, IntegerProperty octaveNum) {
        super(zoom, resolution, fourthInBar, measureNumProperty, trackModell, octaveNum);
        getChildren().add(shapes);
        updateGui();
    }

    @Override
    protected void updateGui() {
        super.updateGui();
        shapes.getChildren().clear();
        createMeasures();
        createVerticalLines();
        createHorizontalLines();
        createTexts();
    }

    private void createTexts() {
        final List<String> noteNames = Arrays.stream(NoteName.values())
                .sorted(Comparator.comparingInt(NoteName::ordinal).reversed())
                .map(noteName -> noteName.name()).collect(Collectors.toList());
        final int increment = GuiConstants.NOTE_LINE_HEIGHT;
        int y = increment;
        for (int i = 0; i <= octaveNum.intValue(); i++) {
            for (int j = 0; j < 12; j++) {
                final Text text = new Text(noteNames.get(j) + " " + (octaveNum.intValue() - i));
                text.setX(5);
                text.setY(y - 3);
                text.setStroke(TrackEditorPanel.TEXT_COLOR);
                text.setFont(Font.font("arial", FontWeight.LIGHT, 8));
                y += increment;
                shapes.getChildren().add(text);
            }
        }
    }

    private void createHorizontalLines() {
        double width = getFullWidth();
        double height = getFullHeight();
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
        shapes.getChildren().add(line);
    }

    private void createVerticalLines() {
        int countOf32ndsInBar = this.get32ndsCountInBar();
        int countOf32nds = countOf32ndsInBar * measureNum.intValue();
        final double w32nds = this.get32ndsWidth();
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
        line.setEndY(getFullHeight());
        if (i % countOf32ndsInBar == 0) {
            line.setStrokeWidth(1);
            line.setStroke(Color.BLACK);
        } else {
            line.setStrokeWidth(0.5);
            line.setStroke(Color.RED);
        }
        shapes.getChildren().addAll(line);
    }

    private void createMeasures() {
        for (int i = 0; i < measureNum.intValue(); i++) {
            createMeasure(i);
        }
    }

    private void createMeasure(int i) {
        double height = getFullHeight();
        double barW = measureWidth * zoom.doubleValue();
        double rectW = barW / fourthInBar.intValue();
        for (int j = 0; j < fourthInBar.intValue(); j++) {
            double rectX = i * barW + j * rectW;
            Rectangle rectangle = new Rectangle(rectX, 0, rectW, height);
            rectangle.setFill(j % 2 == 0 ? Color.LIGHTSKYBLUE : Color.DEEPSKYBLUE);
            shapes.getChildren().add(rectangle);
        }
    }
}
