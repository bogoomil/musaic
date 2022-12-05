package hu.boga.musaic;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import hu.boga.musaic.core.gateway.sequence.SequenceGateway;
import hu.boga.musaic.core.gateway.synth.SynthGateway;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.interactor.SequenceInteractor;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryOut;
import hu.boga.musaic.core.track.interactor.TrackInteractor;
import hu.boga.musaic.core.track.interactor.TrackPropertiesInteractor;
import hu.boga.musaic.gui.sequence.*;
import hu.boga.musaic.gui.track.*;
import hu.boga.musaic.gui.trackeditor.*;
import hu.boga.musaic.midigateway.sequence.SequenceGatewayImpl;
import hu.boga.musaic.midigateway.synth.SynthGatewayImpl;

import javax.inject.Singleton;

public class GuiceModule extends AbstractModule {
    public static final Injector INJECTOR = Guice.createInjector(new GuiceModule());

    @Override
    protected void configure() {

        bind(SequenceBoundaryIn.class).to(SequenceInteractor.class).in(Singleton.class);
        bind(TrackPropertiesBoundaryIn.class).to(TrackPropertiesInteractor.class).in(Singleton.class);
        bind(TrackBoundaryIn.class).to(TrackInteractor.class).in(Singleton.class);

        bind(SequenceServiceImpl.class).in(Singleton.class);
        bind(SequenceBoundaryOut.class).to(SequenceServiceImpl.class);
        bind(SequenceService.class).to(SequenceServiceImpl.class);

        bind(TrackServiceImpl.class).in(Singleton.class);
        bind(TrackPropertiesBoundaryOut.class).to(TrackServiceImpl.class);
        bind(TrackService.class).to(TrackServiceImpl.class);

        bind(TrackEditorServiceImpl.class).in(Singleton.class);
        bind(TrackBoundaryOut.class).to(TrackEditorServiceImpl.class);
        bind(TrackEditorService.class).to(TrackEditorServiceImpl.class);

        install(new FactoryModuleBuilder()
                .implement(TrackPresenter.class, TrackPresenterImpl.class)
                .build(TrackPresenterFactory.class));

        install(new FactoryModuleBuilder()
                .implement(SequencePresenter.class, SequencePresenterImpl.class)
                .build(SequencePresenterFactory.class));

        install(new FactoryModuleBuilder()
                .implement(TrackEditorPresenter.class, TrackEditorPresenterImpl.class)
                .build(TrackEditorPresenterFactory.class));

        bind(SynthGateway.class).to(SynthGatewayImpl.class);
        bind(SequenceGateway.class).to(SequenceGatewayImpl.class).in(Singleton.class);

    }
}
