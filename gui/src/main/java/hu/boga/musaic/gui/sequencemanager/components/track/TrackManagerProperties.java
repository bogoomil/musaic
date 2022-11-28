package hu.boga.musaic.gui.sequencemanager.components.track;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import javafx.beans.property.DoubleProperty;

public class TrackManagerProperties {
    int resolution;
    int fourthInmeasure;
    int measureNum;
    DoubleProperty zoom;
    DoubleProperty scroll;
    String[] colorMappings;
    EventBus eventBus;

    public TrackManagerProperties(int resolution, int fourthInmeasure, int measureNum, DoubleProperty zoom, DoubleProperty scroll, String[] colorMappings, EventBus eventBus) {
        this.resolution = resolution;
        this.fourthInmeasure = fourthInmeasure;
        this.measureNum = measureNum;
        this.zoom = zoom;
        this.scroll = scroll;
        this.colorMappings = colorMappings;
        this.eventBus = eventBus;
    }

    public static class Builder {
        private int resolution;
        private int fourthInmeasure;
        private int measureNum;
        private DoubleProperty zoom;
        private DoubleProperty scroll;
        private String[] colorMappings;
        private EventBus eventBus;

        public Builder withResolution(int resolution) {
            this.resolution = resolution;
            return this;
        }

        public Builder withFourthInmeasure(int fourthInmeasure) {
            this.fourthInmeasure = fourthInmeasure;
            return this;
        }

        public Builder withMeasureNum(int measureNum) {
            this.measureNum = measureNum;
            return this;
        }

        public Builder withZoom(DoubleProperty zoom) {
            this.zoom = zoom;
            return this;
        }

        public Builder withScroll(DoubleProperty scroll) {
            this.scroll = scroll;
            return this;
        }

        public Builder withColorMappings(String[] colorMappings) {
            this.colorMappings = colorMappings;
            return this;
        }

        public Builder withEventBus(EventBus eventBus) {
            this.eventBus = eventBus;
            return this;
        }

        public TrackManagerProperties build() {
            return new TrackManagerProperties(resolution, fourthInmeasure, measureNum, zoom, scroll, colorMappings, eventBus);
        }
    }
}
