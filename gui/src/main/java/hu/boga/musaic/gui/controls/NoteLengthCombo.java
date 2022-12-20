package hu.boga.musaic.gui.controls;

import hu.boga.musaic.musictheory.enums.NoteLength;
import javafx.scene.control.ComboBox;

import java.util.Arrays;
import java.util.List;

public class NoteLengthCombo extends ComboBox<NoteLength> {
    static List<NoteLength> lengths;
    static {
        lengths = Arrays.asList(NoteLength.values());
    }

    public NoteLengthCombo() {
        getItems().addAll(lengths);
    }

    public NoteLength getSelectedNoteLength(){
        return this.getSelectionModel().getSelectedItem();
    }

}
