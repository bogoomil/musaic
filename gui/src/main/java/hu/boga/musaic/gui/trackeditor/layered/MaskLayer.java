package hu.boga.musaic.gui.trackeditor.layered;

import hu.boga.musaic.gui.logic.Observable;
import hu.boga.musaic.musictheory.enums.NoteName;
import hu.boga.musaic.musictheory.enums.Tone;
import javafx.scene.Group;

import java.beans.PropertyChangeEvent;

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

}
