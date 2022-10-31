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
    exports hu.boga.musaic.views;
    exports hu.boga.musaic.controls;

    opens hu.boga.musaic to javafx.fxml, com.google.common;
    opens hu.boga.musaic.views to javafx.fxml, com.google.common;
    opens hu.boga.musaic.controls to javafx.fxml, com.google.common;

}