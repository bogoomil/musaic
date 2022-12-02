package hu.boga.musaic.gui.sequence;

import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nullable;

public interface SequencePresenterFactory {
    SequencePresenter create(@Assisted @Nullable String path);
}
