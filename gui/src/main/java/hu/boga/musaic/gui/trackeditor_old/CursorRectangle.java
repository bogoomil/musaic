package hu.boga.musaic.gui.trackeditor_old;

import hu.boga.musaic.gui.GuiConstants;
import hu.boga.musaic.musictheory.Chord;
import hu.boga.musaic.musictheory.Pitch;
import hu.boga.musaic.musictheory.enums.ChordType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;

public class CursorRectangle extends Pane {
    private static final Logger LOG = LoggerFactory.getLogger(CursorRectangle.class);
    public static final Color FILL_COLOR = Color.color(Color.MAGENTA.getRed(), Color.MAGENTA.getGreen(), Color.MAGENTA.getBlue(), 0.3);
    public static final Color STROKE_COLOR = Color.RED;

    public CursorRectangle() {
        createRectangle(0);
        setVisible(false);
    }
    public void setChordType(final ChordType chordType) {
        getChildren().clear();
        if(chordType != null){
            Chord chord = Chord.getChord(new Pitch(0), chordType);
            Arrays.stream(chord.getPitches()).sorted(Comparator.comparingInt(p -> p.getMidiCode())).forEach(pitch -> {
                LOG.debug("Pitch: {}", pitch.getMidiCode());
                createRectangle(pitch.getMidiCode());
            });
        } else {
             createRectangle(0);
        }
    }

    private void createRectangle(int midiCode) {
        Rectangle rectangle = new Rectangle();
        getChildren().add(rectangle);
        rectangle.setFill(FILL_COLOR);
        rectangle.setStroke(STROKE_COLOR);
        rectangle.setHeight(GuiConstants.LINE_HEIGHT);
        rectangle.setY(-midiCode * GuiConstants.LINE_HEIGHT);
    }

    @Override
    public void setWidth(double width){
        setPrefWidth(width);
        getChildren().stream().map(node -> (Rectangle)node).forEach(rectangle -> rectangle.setWidth(width));
    }
}
