package hu.boga.musaic.gui.track;

import com.google.inject.assistedinject.Assisted;
import javafx.beans.property.DoubleProperty;

public interface TrackPresenterFactory {
    TrackPresenter create(@Assisted TrackModell trackModell,
                          @Assisted("zoom") DoubleProperty zoom,
                          @Assisted("scroll") DoubleProperty scroll);
}
