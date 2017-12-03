package cz.vutbr.fit.pdb.entity.geometry;

import cz.vutbr.fit.pdb.configuration.DrawingMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.java.Log;

@Log
public class LineGeometry implements EntityGeometry {

    private ObservableList<Point> points;

    public LineGeometry(ObservableList<Point> points) {
        this.points = FXCollections.observableArrayList(points);
    }

    @Override
    public boolean containsPoint(double x, double y) {
        log.severe("Not impl yet");
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
