package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.logic.Observable;
import javafx.beans.property.IntegerProperty;

public interface TrackEditorPresenterFactory {
    TrackEditorPresenter create(@Assisted Observable<TrackModell> observable,
                                @Assisted("resolution") IntegerProperty resolution,
                                @Assisted("fourthInBar") IntegerProperty fourthInBar,
                                @Assisted("measureNum") IntegerProperty measureNum,
                                @Assisted("currentMeasure") IntegerProperty currentMeasure,
                                @Assisted("eventBus") EventBus eventBus);

}
