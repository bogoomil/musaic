package hu.boga.musaic.gui.controls;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InstrumentCombo extends ComboBox<String> {

    static List<Instrument> instruments;
    static {
        try {
            instruments = Arrays.asList(MidiSystem.getSynthesizer().getAvailableInstruments());
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public InstrumentCombo() {
        super();
        getItems().addAll(instruments.stream().map(instrument -> instrument.getName()).collect(Collectors.toList()));
    }

    public int getSelectedProgram(){
        Instrument instrument = instruments.get(this.getSelectionModel().getSelectedIndex());
        return instrument != null ? instrument.getPatch().getProgram() : 0;
    }

    public void selectInstrument(int instrumentsProgram){
        getInstrumentIndex(instrumentsProgram).ifPresent(indx -> getSelectionModel().select(indx));
    }

    private Optional<Integer> getInstrumentIndex(int instrumentProgram){
        Optional<Instrument> opt = getInstrumentByData(instrumentProgram);
        return opt.map(instrument -> this.getItems().indexOf(instrument.getName()));
    }

    private static Optional<Instrument> getInstrumentByData(int instrumentsProgram) {
        return instruments.stream().filter(ins -> ins.getPatch().getProgram() == instrumentsProgram).findFirst();
    }

}
