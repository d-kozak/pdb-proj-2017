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

    public ObservableList<Point> getPoints() {
        return points;
    }

    @Override
    public EntityGeometry copyOf() {
        return new PolygonGeometry(FXCollections.observableArrayList(points));
    }

    public double[] getPtArr() {
        double[] array = new double[points.size()*2];
        int i = 0;
        for (Point pt: points) {
            array[i] = pt.getX();
            array[i+1] = pt.getY();
            i += 2;
        }
        return array;
    }
}
