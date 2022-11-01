package hu.boga.musaic;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import hu.boga.musaic.core.gateway.TrackGateway;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.interactor.SequenceInteractor;
import hu.boga.musaic.core.gateway.MidiGateway;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.interactor.TrackInteractor;
import hu.boga.musaic.gui.trackeditor.TrackEditor;
import hu.boga.musaic.midigateway.MidiGatewayImpl;
import hu.boga.musaic.gui.sequenceeditor.SequenceEditor;
import hu.boga.musaic.midigateway.TrackGatewayImpl;

import javax.inject.Singleton;

public class GuiceModule extends AbstractModule {
    public static final Injector INJECTOR = Guice.createInjector(new GuiceModule());

    @Override
    protected void configure() {

        bind(SequenceBoundaryIn.class).to(SequenceInteractor.class);
        bind(SequenceBoundaryOut.class).to(SequenceEditor.class);

        bind(TrackBoundaryIn.class).to(TrackInteractor.class);
        bind(TrackBoundaryOut.class).to(TrackEditor.class);

        bind(MidiGateway.class).to(MidiGatewayImpl.class).in(Singleton.class);
        bind(TrackGateway.class).to(TrackGatewayImpl.class).in(Singleton.class);

    }

}
