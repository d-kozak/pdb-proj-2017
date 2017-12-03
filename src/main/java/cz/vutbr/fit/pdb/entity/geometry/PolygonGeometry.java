package cz.vutbr.fit.pdb.entity.geometry;

import cz.vutbr.fit.pdb.configuration.DrawingMode;
import javafx.collections.ObservableList;

public class PolygonGeometry implements EntityGeometry {
    private ObservableList<Point> points;

    public PolygonGeometry(ObservableList<Point> points) {
        this.points = points;
    }

    @Override
    public DrawingMode getType() {
        return DrawingMode.POLYGON;
    }

    @Override
    public Object getDescription() {
        return points;
    }

    @Override
    public boolean containsPoint(double x, double y) {
        return false;
    }
}
