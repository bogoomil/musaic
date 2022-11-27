package hu.boga.musaic;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import hu.boga.musaic.core.gateway.sequence.SequenceGateway;
import hu.boga.musaic.core.gateway.synth.SynthGateway;
import hu.boga.musaic.core.note.NoteBoundaryIn;
import hu.boga.musaic.core.note.NoteInteractor;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.interactor.SequenceInteractor;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryOut;
import hu.boga.musaic.core.track.interactor.TrackInteractor;
import hu.boga.musaic.core.track.interactor.TrackPropertiesInteractor;
import hu.boga.musaic.gui.sequencemanager.SequenceManager;
import hu.boga.musaic.gui.sequencemanager.components.track.TrackManager;
import hu.boga.musaic.gui.trackeditor.TrackEditor;
import hu.boga.musaic.midigateway.sequence.SequenceGatewayImpl;
import hu.boga.musaic.midigateway.synth.SynthGatewayImpl;

import javax.inject.Singleton;

public class GuiceModule extends AbstractModule {
    public static final Injector INJECTOR = Guice.createInjector(new GuiceModule());

    @Override
    protected void configure() {

        bind(SequenceBoundaryIn.class).to(SequenceInteractor.class);
        bind(SequenceBoundaryOut.class).to(SequenceManager.class);

        bind(TrackBoundaryIn.class).to(TrackInteractor.class);
        bind(TrackBoundaryOut.class).to(TrackEditor.class);

        bind(TrackPropertiesBoundaryIn.class).to(TrackPropertiesInteractor.class);
        bind(TrackPropertiesBoundaryOut.class).to(TrackManager.class);

        bind(NoteBoundaryIn.class).to(NoteInteractor.class).in(Singleton.class);
        bind(SynthGateway.class).to(SynthGatewayImpl.class);

        bind(SequenceGateway.class).to(SequenceGatewayImpl.class).in(Singleton.class);

        bind(EventBus.class).toInstance(new EventBus("MAIN_APP_EVENTBUS"));

    }

}
