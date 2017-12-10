package cz.vutbr.fit.pdb.entity.geometry;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class LineDetails {
    private DoubleProperty length = new SimpleDoubleProperty();

    public double getLength() {
        return length.get();
    }

    public void setLength(double length) {
        this.length.set(length);
    }

    public DoubleProperty lengthProperty() {
        return length;
    }
}
