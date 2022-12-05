package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;

public interface TrackEditorPresenterFactory {
    TrackEditorPresenter create(@Assisted String trackId,
                          @Assisted("resolution") IntegerProperty resolution,
                          @Assisted("fourthInBar") IntegerProperty fourthInBar,
                          @Assisted("measureNum") IntegerProperty measureNum,
                          @Assisted("eventBus") EventBus eventBus);

}
