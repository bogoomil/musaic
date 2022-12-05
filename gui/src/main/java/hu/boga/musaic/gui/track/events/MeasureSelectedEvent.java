package hu.boga.musaic.gui.track.events;

public class MeasureSelectedEvent {
    int selectionStart;
    int selectionEnd;

    public MeasureSelectedEvent(int selectionStart, int selectionEnd) {
        this.selectionStart = selectionStart;
        this.selectionEnd = selectionEnd;
    }

    public int getSelectionStart() {
        return selectionStart;
    }

    public int getSelectionEnd() {
        return selectionEnd;
    }
}
