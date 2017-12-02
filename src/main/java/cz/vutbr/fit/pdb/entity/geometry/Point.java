package cz.vutbr.fit.pdb.entity.geometry;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Point {
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    public Point() {
    }


    public Point(double x, double y) {
        this.x.setValue(x);
        this.y.setValue(y);
    }

    public double getX() {
        return x.get();
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public double getY() {
        return y.get();
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public DoubleProperty yProperty() {
        return y;
    }
}
