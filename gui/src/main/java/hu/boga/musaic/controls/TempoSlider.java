package hu.boga.musaic.controls;

import javafx.scene.control.Slider;

public class TempoSlider extends Slider {
    public TempoSlider(){
        this.setMin(10);
        this.setMax(400);
        this.adjustValue(120);
    }
}
