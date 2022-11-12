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
        this.setCellFactory(new Callback<ListView<Instrument>, ListCell<Instrument>>() {
            class InstrumentListCell extends ListCell<Instrument> {
                @Override protected void updateItem(Instrument item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty && item != null) {
                        setText(item.getName());
                    } else {
                        setText(null);
                    }
                }
            }

            @Override public ListCell<Instrument> call(ListView<Instrument> p) {
                return new InstrumentListCell();
            }
        });
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
