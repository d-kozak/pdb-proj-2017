package cz.vutbr.fit.pdb.entity.geometry;

import cz.vutbr.fit.pdb.configuration.DrawingMode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import lombok.extern.java.Log;

@Log
public class CircleGeometry implements EntityGeometry {
    private Point center;
    private DoubleProperty radius;

    public CircleGeometry(Point center, double radius) {
        this.center = center;
        this.radius = new SimpleDoubleProperty(radius);
    }

    @Override
    public DrawingMode getType() {
        return DrawingMode.CIRCLE;
    }

    @Override
    public Object getDescription() {
        return new Object[]{center, radius};
    }

    @Override
    public boolean containsPoint(double x, double y) {
        log.severe("Not impl yet");
        return false;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public double getRadius() {
        return radius.get();
    }

    public void setRadius(double radius) {
        this.radius.set(radius);
    }

    public DoubleProperty radiusProperty() {
        return radius;
    }
}
