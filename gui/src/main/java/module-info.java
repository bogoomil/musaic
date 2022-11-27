module gui {

    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.guice;
    requires javax.inject;
    requires java.desktop;
    requires com.google.common;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.slf4j;
    requires core;
    requires midigateway;


    exports hu.boga.musaic;
    exports hu.boga.musaic.gui.sequenceeditor;
    exports hu.boga.musaic.gui.trackeditor;
    exports hu.boga.musaic.gui.controls;

    opens hu.boga.musaic to javafx.fxml, com.google.common;
    opens hu.boga.musaic.gui.sequenceeditor to javafx.fxml, com.google.common;
    opens hu.boga.musaic.gui.trackeditor to javafx.fxml, com.google.common, com.google.guice;
    opens hu.boga.musaic.gui.controls to javafx.fxml, com.google.common;
    exports hu.boga.musaic.gui;
    opens hu.boga.musaic.gui to com.google.common, javafx.fxml;
    exports hu.boga.musaic.gui.noteeditor;
    opens hu.boga.musaic.gui.noteeditor to com.google.common, com.google.guice, javafx.fxml;
    exports hu.boga.musaic.gui.sequencemanager;
    opens hu.boga.musaic.gui.sequencemanager to com.google.common, javafx.fxml;
    exports hu.boga.musaic.gui.sequencemanager.components;
    opens hu.boga.musaic.gui.sequencemanager.components to com.google.common, javafx.fxml;
    exports hu.boga.musaic.gui.sequencemanager.components.track;
    opens hu.boga.musaic.gui.sequencemanager.components.track to com.google.common, javafx.fxml;


}