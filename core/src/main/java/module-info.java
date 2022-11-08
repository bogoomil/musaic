module core {
    exports hu.boga.musaic.musictheory.enums;
    exports hu.boga.musaic.core.sequence.boundary;
    exports hu.boga.musaic.core.sequence.boundary.dtos;
    exports hu.boga.musaic.core.sequence.interactor;
    exports hu.boga.musaic.core.modell;
    exports hu.boga.musaic.core.exceptions;
    exports hu.boga.musaic.musictheory;
    exports hu.boga.musaic.core.track.boundary;
    exports hu.boga.musaic.core.track.interactor;
    exports hu.boga.musaic.core.track.boundary.dtos;
    exports hu.boga.musaic.core.gateway.sequence;
    exports hu.boga.musaic.core.modell.events;

    requires com.google.guice;
    requires javax.inject;
    requires java.desktop;
    requires com.google.common;
    requires org.slf4j;
}