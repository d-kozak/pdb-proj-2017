package cz.vutbr.fit.pdb.entity.geometry;

import cz.vutbr.fit.pdb.configuration.DrawingMode;
import lombok.extern.java.Log;

import static cz.vutbr.fit.pdb.utils.MathUtils.distance;
import static java.lang.String.format;

@Log
public class PointGeometry implements EntityGeometry {

    private Point point;

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
    public boolean containsPoint(double x, double y) {
        double distance = distance(x, y, point.getX(), point.getY());
        log.info(format("Distance between [%f,%f] and [%f,%f] is %f", x, y, point.getX(), point.getY(), distance));
        return distance < 10;
    }

    public String focus() {
        return "Touched!";
    }
}
