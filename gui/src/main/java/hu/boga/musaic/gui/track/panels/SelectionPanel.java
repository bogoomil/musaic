package hu.boga.musaic.gui.track.panels;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.gui.panels.ZoomablePanel;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.track.events.MeasureSelectedEvent;
import hu.boga.musaic.gui.track.events.TrackEditingFinishedEvent;
import hu.boga.musaic.gui.trackeditor.TrackEditorPresenter;
import hu.boga.musaic.gui.trackeditor.TrackEditorPresenterFactory;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class SelectionPanel extends NotesPanelBase {

    private static final Logger LOG = LoggerFactory.getLogger(SelectionPanel.class);
    private final EventBus eventBus;
    private int selectionStartInTicks, selectionEndInTicks;
    private final TrackEditorPresenterFactory trackEditorPresenterFactory;
    private int currentMeasureIndex;

    public SelectionPanel(int height,
                          DoubleProperty zoom,
                          DoubleProperty scroll,
                          IntegerProperty resolution,
                          IntegerProperty fourthInBar,
                          IntegerProperty measureNum,
                          TrackModell trackModell,
                          EventBus eventBus,
                          TrackEditorPresenterFactory trackEditorPresenterFactory) {
        super(height, zoom, scroll, resolution, fourthInBar, measureNum, trackModell);
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.trackEditorPresenterFactory = trackEditorPresenterFactory;
        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> mouseClicked(event));
    }

    @Override
    protected void updateGui() {
        createBorder();
        double x = getXByTick(selectionStartInTicks);
        int width = (int) (getXByTick(selectionEndInTicks) - x);

        Rectangle rectangle = new Rectangle((int) getXByTick(selectionStartInTicks), 0, width, height);
        rectangle.setFill(Color.color(Color.DARKORCHID.getRed(), Color.DARKORCHID.getGreen(), Color.DARKORCHID.getBlue(), 0.4));
        getChildren().add(rectangle);
    }

    private void mouseClicked(MouseEvent event) {
        currentMeasureIndex = getTickAtX(event.getX()) / (fourthInBar.intValue() * resolution.intValue());
        int selectionStart = currentMeasureIndex * resolution.intValue() * fourthInBar.intValue();
        int selectionEnd = selectionStart + (fourthInBar.intValue() * resolution.intValue());

        if (event.getClickCount() == 2) {
            openTrackEditor();
        } else if (event.getClickCount() == 1) {
            eventBus.post(new MeasureSelectedEvent(selectionStart, selectionEnd));
        }
    }

    private void openTrackEditor() {
        LOG.debug("creating editor: {}", trackModell.id);
        createTrackEditor();
    }

    @Subscribe
    void handle(MeasureSelectedEvent event) {
        this.selectionStartInTicks = event.getSelectionStart();
        this.selectionEndInTicks = event.getSelectionEnd();
        updateGui();
    }

    private void createTrackEditor() {
        FXMLLoader loader = new FXMLLoader(TrackEditorPresenter.class.getResource("track-editor.fxml"));
        loader.setControllerFactory(c -> trackEditorPresenterFactory.create(
                trackModell.id,
                resolution,
                fourthInBar,
                new SimpleIntegerProperty(measureNum.intValue()),
                new SimpleIntegerProperty(0),
                eventBus));
        createWindow(tryLoadingPane(loader));
    }

    private Pane tryLoadingPane(FXMLLoader loader) {
        BorderPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
    }

    private void createWindow(Pane pane) {
        Stage newWindow = new Stage();
        newWindow.setScene(new Scene(pane));
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.initOwner(null);
        newWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                LOG.debug("track editor closing");
                eventBus.post(new TrackEditingFinishedEvent(trackModell.id));
            }
        });
        newWindow.show();
    }

}
