package hu.boga.musaic.gui.track;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;

public interface TrackPresenterFactory {
    TrackPresenter create(@Assisted TrackModell trackModell, @Assisted EventBus eventBus);
}
