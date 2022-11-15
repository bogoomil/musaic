package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteLength;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

public class SettingsContextMenu extends ContextMenu {
    EventBus eventBus;
    public int currentTick;

    public SettingsContextMenu(EventBus eventBus) {
        this.eventBus = eventBus;
        buildGui();
    }

    private void buildGui() {
        MenuItem addLoopStartPoint = new MenuItem("Set loop start");
        addLoopStartPoint.addEventHandler(ActionEvent.ACTION, event -> eventBus.post(new LoopStartEvent(currentTick)));
        MenuItem addLoopEndPoint = new MenuItem("Set loop end");
        addLoopEndPoint.addEventHandler(ActionEvent.ACTION, event -> eventBus.post(new LoopEndEvent(currentTick)));
        MenuItem clearLoop = new MenuItem("Clear loop");

        clearLoop.addEventHandler(ActionEvent.ACTION, event -> eventBus.post(new ClearLoopEvent()));


              Menu length =  new Menu("Length", null, createNoteLengthMenuItem());
              Menu chords =   new Menu("Chords", null, createChordMenuItems());

        MenuItem selectAllMenu = new MenuItem("Select all");
        selectAllMenu.addEventHandler(ActionEvent.ACTION, event -> eventBus.post(new SelectAllEvent()));
        MenuItem deSelectAllMenu = new MenuItem("Deselect all");
        deSelectAllMenu.addEventHandler(ActionEvent.ACTION, event -> eventBus.post(new DeSelectAllEvent()));
        MenuItem invertSelectionMenu = new MenuItem("Invert selection");
        invertSelectionMenu.addEventHandler(ActionEvent.ACTION, event -> eventBus.post(new InvertSelectionEvent()));
        Menu selectionMenu = new Menu("Selection", null,
                selectAllMenu,
                deSelectAllMenu,
                invertSelectionMenu
        );

        MenuItem deleteSelectedNotesMenu = new MenuItem("selected");
        deleteSelectedNotesMenu.addEventHandler(ActionEvent.ACTION, event -> eventBus.post(new DeleteSelectedNoteEvent()));
        MenuItem deleteAllNotesMenu = new MenuItem("all");
        deleteAllNotesMenu.addEventHandler(ActionEvent.ACTION, event -> eventBus.post(new DeleteAllNotesEvent()));
        Menu deletionMenu = new Menu("Delete", null,
                deleteSelectedNotesMenu,
                deleteAllNotesMenu
        );
        getItems().add(addLoopStartPoint);
        getItems().add(addLoopEndPoint);
        getItems().add(clearLoop);
        getItems().add(new SeparatorMenuItem());
        getItems().add(length);
        getItems().add(chords);
        getItems().add(new SeparatorMenuItem());
        getItems().add(selectionMenu);
        getItems().add(deletionMenu);
    }

    private RadioMenuItem[] createChordMenuItems() {
        final ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem[] items = new RadioMenuItem[ChordType.values().length + 1];
        items[0] = new RadioMenuItem("single note");
        items[0].setToggleGroup(toggleGroup);
        items[0].setSelected(true);
        items[0].addEventHandler(ActionEvent.ACTION, event -> eventBus.post(new ChordTypeChangedEvent(null)));
        for (int i = 0; i < ChordType.values().length; i++) {
            createChordTypeChangeMenuItem(toggleGroup, items, i);
        }
        return items;
    }

    private void createChordTypeChangeMenuItem(ToggleGroup toggleGroup, RadioMenuItem[] items, int i) {
        ChordType currChordType = ChordType.values()[i];
        RadioMenuItem menuItem = new RadioMenuItem(currChordType.name());
        menuItem.setToggleGroup(toggleGroup);
        int finalI = i;
        menuItem.addEventHandler(ActionEvent.ACTION, event -> eventBus.post(new ChordTypeChangedEvent(currChordType)));
        items[i + 1] = menuItem;
    }

    private RadioMenuItem[] createNoteLengthMenuItem() {
        final ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem[] items = new RadioMenuItem[NoteLength.values().length];
        for (int i = 0; i < NoteLength.values().length; i++) {
            createNoteLengthChangeMenuItem(toggleGroup, items, i);
        }
        items[0].setSelected(true);
        return items;
    }

    private void createNoteLengthChangeMenuItem(ToggleGroup toggleGroup, RadioMenuItem[] items, int i) {
        NoteLength currLength = NoteLength.values()[i];
        RadioMenuItem menuItem = new RadioMenuItem(currLength.name());
        menuItem.setToggleGroup(toggleGroup);
        int finalI = i;
        menuItem.addEventHandler(ActionEvent.ACTION, event -> eventBus.post(new NoteLengthChangedEvent(currLength)));
        items[i] = menuItem;
    }


    public static class NoteLengthChangedEvent{
        NoteLength noteLength;

        public NoteLengthChangedEvent(NoteLength noteLength) {
            this.noteLength = noteLength;
        }

        public NoteLength getNoteLength() {
            return noteLength;
        }
    }

    public static class ChordTypeChangedEvent{
        ChordType chordType;

        public ChordTypeChangedEvent(ChordType chordType) {
            this.chordType = chordType;
        }

        public ChordType getChordType() {
            return chordType;
        }
    }

    public static class DeleteAllNotesEvent{}

    public static class DeleteSelectedNoteEvent{}

    public static class InvertSelectionEvent{}

    public static class SelectAllEvent{}

    public static class DeSelectAllEvent{}

    public static class LoopStartEvent{
        public double tick;

        public LoopStartEvent(double tick) {
            this.tick = tick;
        }
    }

    public static class LoopEndEvent{
        public double tick;

        public LoopEndEvent(double tick) {
            this.tick = tick;
        }
    }

    public static class ClearLoopEvent{}
}
