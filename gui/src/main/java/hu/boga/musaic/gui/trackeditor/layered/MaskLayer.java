package hu.boga.musaic.gui.trackeditor.layered;

import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.gui.logic.Observable;
import hu.boga.musaic.musictheory.Scale;
import hu.boga.musaic.musictheory.enums.NoteName;
import hu.boga.musaic.musictheory.enums.Tone;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.util.List;

public class MaskLayer extends Group implements Layer{

    private static final Logger LOG = LoggerFactory.getLogger(MaskLayer.class);

    private final LayeredPane parent;

    private Tone currentTone;
    private NoteName currentRoot;

    public MaskLayer(LayeredPane parent, Observable<NoteName> rootObservable, Observable<Tone> toneObservable) {
        this.parent = parent;
        rootObservable.addPropertyChangeListener(this::rootChanged);
        toneObservable.addPropertyChangeListener(this::toneChanged);
    }

    @Override
    public void updateGui() {
        getChildren().clear();
        paintDisabled();
    }

    private void paintDisabled() {
        if (currentTone != null && currentRoot != null) {
            List<NoteName> scale = Scale.getScale(currentRoot, currentTone);
            LOG.debug("scale: {}", scale);
            for (int y = 0; y < parent.getFullHeight(); y += GuiConstants.NOTE_LINE_HEIGHT) {
                paintDisabledLines(scale, y);
            }
        }
    }

    private void toneChanged(PropertyChangeEvent propertyChangeEvent) {
        this.currentTone = (Tone) propertyChangeEvent.getNewValue();
        updateGui();
    }

    private void rootChanged(PropertyChangeEvent propertyChangeEvent) {
        this.currentRoot = (NoteName) propertyChangeEvent.getNewValue();
        updateGui();
    }

    private void paintDisabledLines(List<NoteName> scale, int y) {
        LOG.debug("paint mask: {}", y);
        NoteName currentNoteName = NoteName.byCode(parent.getPitchByY(y + 5).getMidiCode());
        if (!scale.contains(currentNoteName)) {
            LOG.debug("disabled note: {}", currentNoteName.name());
            paintDisabledRectangle(y);
        }
        if (currentNoteName == currentRoot) {
            paintRootMarker(y);
        }
    }

    private void paintRootMarker(int y) {
        Color highLight = Color.CYAN;
        Rectangle rectangle = new Rectangle(0, y + 2, parent.getFullWidth(), GuiConstants.NOTE_LINE_HEIGHT - 2);
        rectangle.setStroke(highLight);
        rectangle.setFill(Color.color(highLight.getRed(), highLight.getGreen(), highLight.getBlue(), 0.4));
        rectangle.setMouseTransparent(true);
        getChildren().add(rectangle);
    }

    private void paintDisabledRectangle(int y) {
        final Rectangle rectangle = new Rectangle();
        rectangle.setMouseTransparent(false);
        rectangle.setFill(Color.GRAY);
        rectangle.setX(0);
        rectangle.setY(y);
        rectangle.setWidth(parent.getFullWidth());
        rectangle.setHeight(GuiConstants.NOTE_LINE_HEIGHT);
        getChildren().add(rectangle);
    }
}
