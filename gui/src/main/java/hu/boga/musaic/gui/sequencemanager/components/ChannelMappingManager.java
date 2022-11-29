package hu.boga.musaic.gui.sequencemanager.components;

import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.gui.controls.InstrumentCombo;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ChannelMappingManager {
    SequenceBoundaryIn boundaryIn;
    SequenceDto sequenceDto;

    public ChannelMappingManager(SequenceBoundaryIn boundaryIn, SequenceDto sequenceDto) {
        this.boundaryIn = boundaryIn;
        this.sequenceDto = sequenceDto;
    }

    public VBox create(){
        VBox vBox = new VBox();
        for (int i = 0; i < 16; i++) {
            HBox hBox = new HBox();

            Label l = new Label("" + i);
            hBox.getChildren().add(l);

            InstrumentCombo instrCombo = new InstrumentCombo();
            instrCombo.selectInstrument(sequenceDto.channelToProgramMappings[i]);

            int finalI = i;
            instrCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
                boundaryIn.updateChannelToProgramMappings(sequenceDto.id, finalI, instrCombo.getSelectedProgram());
            });
            hBox.getChildren().add(instrCombo);
            ColorPicker colorPicker = new ColorPicker();
            hBox.getChildren().add(colorPicker);
            colorPicker.setOnAction(event -> {
            });
            vBox.getChildren().add(hBox);
        }
        return vBox;
    }
}
