package hu.boga.musaic.gui.trackeditor.layered;

import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.gui.logic.Observable;
import hu.boga.musaic.musictheory.Scale;
import hu.boga.musaic.musictheory.enums.NoteName;
import hu.boga.musaic.musictheory.enums.Tone;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.beans.PropertyChangeEvent;
import java.util.List;

public class MaskLayer extends Group implements Layer{

    private final LayeredPane parent;
    private final Observable<NoteName> rootObservable;
    private final Observable<Tone> toneObservable;

    private Tone currentTone;
    private NoteName currentRoot;

    public MaskLayer(LayeredPane parent, Observable<NoteName> rootObservable, Observable<Tone> toneObservable) {
        this.parent = parent;

        rootObservable.addPropertyChangeListener(propertyChangeEvent -> rootChanged(propertyChangeEvent));
        toneObservable.addPropertyChangeListener(propertyChangeEvent -> tonehanged(propertyChangeEvent));

        this.rootObservable = rootObservable;
        this.toneObservable = toneObservable;

    }

    @Override
    public void updateGui() {
        getChildren().clear();


    }

    private void tonehanged(PropertyChangeEvent propertyChangeEvent) {
        this.currentTone = (Tone) propertyChangeEvent.getNewValue();
        updateGui();
    }

    private void rootChanged(PropertyChangeEvent propertyChangeEvent) {
        this.currentRoot = (NoteName) propertyChangeEvent.getNewValue();
        updateGui();
    }

    private void paintDisabled() {
        if (currentTone != null && currentRoot != null) {
            List<NoteName> scale = Scale.getScale(currentRoot, currentTone);
            for (int y = 0; y < parent.getFullHeight(); y += GuiConstants.NOTE_LINE_HEIGHT) {
                paintDisabledLines(scale, y);
            }
        }
    }


    private void paintDisabledLines(List<NoteName> scale, int y) {
        NoteName currentNoteName = NoteName.byCode(parent.getPitchByY(y + 5).getMidiCode());
        if (!scale.contains(currentNoteName)) {
            paintDisabledRectangle(y);
        }
        if (currentNoteName == currentRoot) {
            paintRootMarker(y);
        }
    }


    private void paintDisabledRectangle(int y) {
        final Rectangle rectangle = new Rectangle();
        rectangle.addEventHandler(MouseEvent.ANY, event -> event.consume());
        rectangle.setFill(DISABLED_COLOR);
        rectangle.setX(0);
        rectangle.setY(y);
        rectangle.setWidth(getPrefWidth());
        rectangle.setHeight(getPitchHeight());
        getChildren().add(rectangle);
    }


}
