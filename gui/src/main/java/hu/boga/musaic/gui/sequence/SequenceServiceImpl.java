package hu.boga.musaic.gui.sequence;

import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class SequenceServiceImpl implements SequenceService, SequenceBoundaryOut {

    private static final Logger LOG = LoggerFactory.getLogger(SequenceService.class);

    @Inject
    private SequenceBoundaryIn boundaryIn;
    private SequenceDto dto;

    @Override
    public void displaySequence(SequenceDto dto) {
        this.dto = dto;
        LOG.debug("displayed dto: {}, {}", this.dto, this);
    }

    public void create(){
        boundaryIn.create();
    }

    public void open(String path){
        LOG.debug("open: {} {}", path, this);
        boundaryIn.open(path);
        LOG.debug("done...{}", this);

    }

    public SequenceDto getSequenceDto() {
        return dto;
    }
}
