package cz.vutbr.fit.pdb.entity.geometry;

import cz.vutbr.fit.pdb.configuration.DrawingMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.java.Log;

import static cz.vutbr.fit.pdb.utils.MathUtils.distance;
import static java.lang.String.format;

@Log
public class LineGeometry implements EntityGeometry {

    private ObservableList<Point> points;

    public LineGeometry(ObservableList<Point> points) {
        this.points = FXCollections.observableArrayList(points);
    }

    @Override
    public boolean containsPoint(double x, double y) {
        log.severe("Hacked, distance from corner points only!");
        for (Point point : points) {
            double distance = distance(x, y, point.getX(), point.getY());
            if (distance < 10) {
                log.info(format("Distance between [%f,%f] and [%f,%f] is %f", x, y, point.getX(), point.getY(), distance));
                return true;
            }
        }
        return false;
    }

    @Override
    public DrawingMode getType() {
        return DrawingMode.LINE;
    }

    @Override
    public Object getDescription() {
        return points;
    }
}
