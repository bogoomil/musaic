package hu.boga.musaic;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.interactor.SequenceInteractor;
import hu.boga.musaic.gateway.MidiGateway;
import hu.boga.musaic.midigateway.MidiGatewayImpl;
import hu.boga.musaic.views.SequenceEditorPanelController;

public class GuiceModule extends AbstractModule {
    public static final Injector INJECTOR = Guice.createInjector(new GuiceModule());

    @Override
    protected void configure() {

//        bind(SequenceGateway.class).to(SequenceGatewayImpl.class);
//        bind(TrackGateway.class).to(TrackGatewayImpl.class);
//
        bind(SequenceBoundaryIn.class).to(SequenceInteractor.class);
        bind(SequenceBoundaryOut.class).to(SequenceEditorPanelController.class);

        bind(MidiGateway.class).to(MidiGatewayImpl.class);
//
//        bind(TrackBoundaryIn.class).to(TrackInteractor.class);
//        bind(TrackBoundaryOut.class).to(TrackEditorPanelController.class);

    }

}
