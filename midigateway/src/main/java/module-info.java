module midigateway {
    exports hu.boga.musaic.midigateway;
    exports hu.boga.musaic.midigateway.utils;
    exports hu.boga.musaic.midigateway.converters;
    exports hu.boga.musaic.midigateway.sequence;

    requires java.desktop;
    requires core;
    requires javax.inject;
    requires org.slf4j;

    opens hu.boga.musaic.midigateway to com.google.guice;

    opens hu.boga.musaic.midigateway.converters to com.google.guice;
    opens hu.boga.musaic.midigateway.sequence to com.google.guice;
    opens hu.boga.musaic.midigateway.utils to com.google.guice;
    exports hu.boga.musaic.midigateway.synth;


}