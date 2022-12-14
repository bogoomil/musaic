package hu.boga.musaic.gui.trackeditor.panels;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Observable<T> {
    private PropertyChangeSupport support;
    T value;
    String name;

    public Observable(String name) {
        this.name = name;
        support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setValue(T value) {
        support.firePropertyChange(name, this.value, value);
        this.value = value;
    }

    public String getName() {
        return name;
    }
}
