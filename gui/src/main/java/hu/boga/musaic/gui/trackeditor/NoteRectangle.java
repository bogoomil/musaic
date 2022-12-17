package hu.boga.musaic.gui.trackeditor;

import hu.boga.musaic.gui.trackeditor.events.DeleteNoteEvent;
import hu.boga.musaic.gui.trackeditor.layered.NoteChangedListener;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class NoteRectangle extends Rectangle {
    private static final Logger LOG = LoggerFactory.getLogger(NoteRectangle.class);
    private static  final Color COLOR = Color.BLACK;
    private Color fill;
    private Color selectedColor;

    private boolean selected;

    private NoteModell noteModell;
    private static final List<String> selectedIds = new ArrayList<>();
    private final NoteChangedListener noteChangedListener;

    public NoteRectangle(NoteModell noteModell, NoteChangedListener noteChangedListener) {
        this.noteModell = noteModell;
        this.selected = selectedIds.contains(noteModell.id);
        this.noteChangedListener = noteChangedListener;
        selectedColor = Color.color(Color.LAWNGREEN.getRed(), Color.LAWNGREEN.getGreen(), Color.LAWNGREEN.getBlue(), noteModell.velocity);
        fill = selected ? selectedColor :  Color.color(COLOR.getRed(), COLOR.getGreen(), COLOR.getBlue(), noteModell.velocity);
        this.setStroke(COLOR);
        this.setStrokeWidth(3);
        this.setFill(fill);
        this.setArcHeight(15);
        this.setArcWidth(15);

        setOnMouseClicked(this::handleMouseClicked);
    }

    private void handleMouseClicked(MouseEvent event) {
        if(event.getButton() == MouseButton.SECONDARY){
            showContextMenu(event);
        }else if (event.getClickCount() == 2) {
            noteChangedListener.noteDeleted(noteModell.id);
        } else if (event.getClickCount() == 1) {
            toggleSlection();
        }
        event.consume();
    }

    private void showContextMenu(MouseEvent event) {
        getContextMenu().show(this, event.getScreenX(), event.getScreenY());
        event.consume();
    }

    private ContextMenu getContextMenu(){
        ContextMenu menu = new ContextMenu();

        int currVelocity = (int) (noteModell.velocity * 100);

        Label label = new Label("" + currVelocity);

        Slider volumeSlider = new Slider();
        volumeSlider.orientationProperty().setValue(Orientation.VERTICAL);
        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(noteModell.velocity);
        volumeSlider.setMajorTickUnit(0.1);
        volumeSlider.setSnapToTicks(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.blockIncrementProperty().setValue(0.1);
        volumeSlider.setMinorTickCount(10);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            noteModell.velocity = newValue.doubleValue();
            fill = Color.color(fill.getRed(), fill.getGreen(), fill.getBlue(), noteModell.velocity);
            label.setText(((int) (noteModell.velocity * 100)) + "%");
            setFill(fill);
        });

        VBox hBox = new VBox();
        hBox.getChildren().add(volumeSlider);
        hBox.getChildren().add(label);

        MenuItem volumeMenuItem = new MenuItem("", hBox);
        menu.getItems().add(volumeMenuItem);
        return menu;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public int getTick() {
        return (int) this.noteModell.tick;
    }

    public int getPitch() {
        return this.noteModell.midiCode;
    }

    public void toggleSlection() {
        this.selected = !selected;
        if(selected){
            selectedIds.add(noteModell.id);
            setFill(selectedColor);
        }else{
            selectedIds.remove(noteModell.id);
            Color origColor = (Color) getStroke();
            setFill(Color.color(origColor.getRed(), origColor.getGreen(), origColor.getBlue(), noteModell.velocity));
        }
    }

    public String getNoteId(){
        return noteModell.id;
    }

}
