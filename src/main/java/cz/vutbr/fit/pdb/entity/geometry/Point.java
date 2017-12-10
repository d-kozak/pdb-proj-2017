package cz.vutbr.fit.pdb.entity.geometry;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.Optional;

public class Point {
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    public Point() {
    }


    public Point(double x, double y) {
        this.x.setValue(x);
        this.y.setValue(y);
    }

    public static Optional<Point> of(String x, String y) {
        try {
            double xDouble = Double.parseDouble(x);
            double yDouble = Double.parseDouble(y);
            return Optional.of(new Point(xDouble, yDouble));
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != null ? !x.equals(point.x) : point.x != null) return false;
        return y != null ? y.equals(point.y) : point.y == null;
    }

    @Override
    public int hashCode() {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        return result;
    }

    public Point copyOf() {
        return new Point(this.getX(), this.getY());
    }
}
