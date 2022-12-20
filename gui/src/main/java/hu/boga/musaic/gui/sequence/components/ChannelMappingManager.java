package hu.boga.musaic.gui.sequence.components;

import hu.boga.musaic.gui.controls.InstrumentCombo;
import hu.boga.musaic.gui.sequence.SequenceModell;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ChannelMappingManager {

    SequenceModell modell;

    List<ChannelMappingChangeListener> listenerList = new ArrayList<>();

    public ChannelMappingManager(SequenceModell modell) {
        this.modell = modell;
    }

    public VBox create(){
        VBox vBox = new VBox();
        for (int i = 0; i < 16; i++) {
            HBox hBox = new HBox();

            Label l = new Label("" + i);
            hBox.getChildren().add(l);

            InstrumentCombo instrCombo = new InstrumentCombo();
            instrCombo.selectInstrument(modell.channelToProgramMappings[i]);

            int finalI = i;
            instrCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
                notifyListeners(finalI, instrCombo.getSelectedProgram());
            });
            hBox.getChildren().add(instrCombo);
            if (SequenceModell.COLOR_MAPPING[i] != null) {
                instrCombo.setBackground(new Background(
                        new BackgroundFill(
                                Color.web(SequenceModell.COLOR_MAPPING[i]),
                                CornerRadii.EMPTY,
                                Insets.EMPTY)));
            }
            vBox.getChildren().add(hBox);
        }
        return vBox;
    }

    public void addChannelMappingChangeListener(ChannelMappingChangeListener listener){
        this.listenerList.add(listener);
    }

    private void notifyListeners(int channel, int program){
        listenerList.forEach(listener -> listener.mappingChanged(channel, program));
    }
}
