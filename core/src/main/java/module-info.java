module core {
    exports hu.boga.musaic.musictheory.enums;
    exports hu.boga.musaic.core.sequence.boundary;
    exports hu.boga.musaic.core.sequence.boundary.dtos;
    exports hu.boga.musaic.core.sequence.interactor;
    exports hu.boga.musaic.core.gateway;
    exports hu.boga.musaic.core.modell;
    exports hu.boga.musaic.core.exceptions;

    requires com.google.guice;
    requires javax.inject;
    requires java.desktop;
    requires com.google.common;
    requires org.slf4j;
}