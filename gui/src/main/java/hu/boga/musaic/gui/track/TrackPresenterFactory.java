package hu.boga.musaic.gui.track;

import com.google.inject.assistedinject.Assisted;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;

public interface TrackPresenterFactory {
    TrackPresenter create(@Assisted String trackId,
                          @Assisted("zoom") DoubleProperty zoom,
                          @Assisted("scroll") DoubleProperty scroll,
                          @Assisted("resolution") IntegerProperty resolution,
                          @Assisted("fourthInBar") IntegerProperty fourthInBar);
}
