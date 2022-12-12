package hu.boga.musaic.gui.controls;

import hu.boga.musaic.musictheory.enums.ChordType;
import javafx.scene.control.ComboBox;

import java.util.Arrays;
import java.util.List;

public class ChordTypeCombo extends ComboBox<ChordType> {
    static List<ChordType> types;
    static {
        types = Arrays.asList(ChordType.values());
    }

    public ChordTypeCombo() {
        getItems().addAll(types);
    }

    public ChordType getSelectedChordType(){
        return this.getSelectionModel().getSelectedItem();
    }

}
