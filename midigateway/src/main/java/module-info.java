module midigateway {
    exports hu.boga.musaic.midigateway;
    exports hu.boga.musaic.midigateway.utils;
    requires java.desktop;
    requires core;
    requires javax.inject;
    requires org.slf4j;

    opens hu.boga.musaic.midigateway to com.google.guice;
}