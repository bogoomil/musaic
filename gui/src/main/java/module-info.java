module gui {

    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.guice;
    requires com.google.guice.extensions.assistedinject;
    requires javax.inject;
    requires java.desktop;
    requires com.google.common;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.slf4j;
    requires core;
    requires midigateway;
    requires jsr305;

    exports hu.boga.musaic;
    exports hu.boga.musaic.gui;
    exports hu.boga.musaic.gui.controls;

    opens hu.boga.musaic to javafx.fxml, com.google.common;
    opens hu.boga.musaic.gui to com.google.common, javafx.fxml;

    opens hu.boga.musaic.gui.sequence to javafx.fxml, com.google.guice, com.google.inject, com.google.common;
    exports hu.boga.musaic.gui.sequence to com.google.guice, com.google.inject;
    exports hu.boga.musaic.gui.track to javafx.fxml, com.google.guice;
    opens hu.boga.musaic.gui.track to javafx.fxml, com.google.common;
    exports hu.boga.musaic.gui.track.events to com.google.guice, javafx.fxml;
    opens hu.boga.musaic.gui.track.events to javafx.fxml;
    opens hu.boga.musaic.gui.track.panels to com.google.common;
    exports hu.boga.musaic.gui.trackeditor to com.google.guice;
    opens hu.boga.musaic.gui.trackeditor to javafx.fxml;

    opens hu.boga.musaic.gui.panels to com.google.common;
}