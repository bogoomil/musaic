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
    exports hu.boga.musaic.gui.views;
    exports hu.boga.musaic.gui.controls;

    opens hu.boga.musaic to javafx.fxml, com.google.common;
    opens hu.boga.musaic.gui.views to javafx.fxml, com.google.common;
    opens hu.boga.musaic.gui.controls to javafx.fxml, com.google.common;
    exports hu.boga.musaic.gui;
    opens hu.boga.musaic.gui to com.google.common, javafx.fxml;

}