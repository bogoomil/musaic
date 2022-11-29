package hu.boga.musaic.gui.sequence;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class SequencePresenter {
    private static final Logger LOG = LoggerFactory.getLogger(SequencePresenter.class);
    @FXML
    private Label label;

    SequenceService service;

    @Inject
    public SequencePresenter(SequenceService service) {
        this.service = service;
    }

    public void initialize() {
        System.out.println("init seq prezi");
        System.out.println("service: " + service);
    }

    public void open(String path) {
        LOG.debug("open 1 {}", this);
        service.open(path);
        LOG.debug("open 2 {} {}", service.getSequenceDto(), this);
    }

    public void create() {
        service.create();
        System.out.println("seq created: " + service.getSequenceDto().id);
    }
}
