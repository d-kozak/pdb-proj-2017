package cz.vutbr.fit.pdb.entity.geometry;

import cz.vutbr.fit.pdb.configuration.DrawingMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.java.Log;

@Log
public class PolygonGeometry implements EntityGeometry {
    private ObservableList<Point> points;

    public PolygonGeometry(ObservableList<Point> points) {
        this.points = FXCollections.observableArrayList(points);
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
        log.severe("Not implemented yet");
        return false;
    }
}
