package hu.boga.musaic.gui.sequence;

import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class SequenceServiceImpl implements SequenceService, SequenceBoundaryOut {

    private static final Logger LOG = LoggerFactory.getLogger(SequenceService.class);
    private SequenceBoundaryIn boundaryIn;
    private SequenceDto dto;

    @Inject
    public SequenceServiceImpl(SequenceBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
    }

    @Override
    public void displaySequence(SequenceDto dto) {
        this.dto = dto;
        LOG.debug("displayed dto: {}, {}", this.dto, this);
    }

    @Override
    public void create() {
        boundaryIn.create();
    }

    @Override
    public void open(String path) {
        LOG.debug("open: {} {}", path, this);
        boundaryIn.open(path);
        LOG.debug("done...{}", this);
    }

    @Override
    public SequenceModell getSequence() {
        return new SequenceDtoToModellConverter(dto).convert();
    }

    @Override
    public void updateChannelMapping(String id, int channel, int program) {
        boundaryIn.updateChannelToProgramMappings(id, channel, program);
    }

    @Override
    public void save(String id, String path) {
        boundaryIn.save(id, path);
    }
}
