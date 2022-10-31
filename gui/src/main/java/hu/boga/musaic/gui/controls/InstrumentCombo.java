package hu.boga.musaic.gui.controls;

import javafx.scene.control.ComboBox;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class InstrumentCombo extends ComboBox<Instrument> {

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
        getItems().addAll(instruments);
//        setConverter(new StringConverter<Instrument>() {
//            @Override
//            public String toString(Instrument object) {
//                return object.getName();
//            }
//
//            @Override
//            public Instrument fromString(String string) {
//                return null;
//            }
//        });
    }

    public static Optional<Instrument> getInstrumentByData(int instrumentsProgram) {
       return instruments.stream().filter(ins -> ins.getPatch().getProgram() == instrumentsProgram).findFirst();
    }

    public Optional<Integer> getInstrumentIndex(int instrumentProgram){
        Optional<Instrument> opt = getInstrumentByData(instrumentProgram);
        return opt.map(instrument -> this.getItems().indexOf(instrument));
    }

    public void selectInstrument(int instrumentsProgram){
        getInstrumentIndex(instrumentsProgram).ifPresent(indx -> getSelectionModel().select(indx));
    }

    public int getSelectedProgram(){
        Instrument instrument = this.getSelectionModel().getSelectedItem();
        return instrument != null ? instrument.getPatch().getProgram() : 0;
    }
}
