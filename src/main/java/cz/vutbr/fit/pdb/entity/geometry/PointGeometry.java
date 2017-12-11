package cz.vutbr.fit.pdb.entity.geometry;

import cz.vutbr.fit.pdb.configuration.DrawingMode;
import lombok.extern.java.Log;

@Log
public class PointGeometry implements EntityGeometry {

    private Point point;

    public PointGeometry(double x, double y) {
        this.point = new Point(x, y);
    }

    public double getX() {
        return point.getX();
    }

    public double getY() {
        return point.getY();
    }

    public PointGeometry(Point point) {
        this.point = point;
    }

    @Override
    public DrawingMode getType() {
        return DrawingMode.POINT;
    }

    @Override
    public Object getDescription() {
        return point;
    }

    @Override
    public EntityGeometry copyOf() {
        return new PointGeometry(point.getX(), point.getY());
    }
}
