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
    public DrawingMode getType() {
        return DrawingMode.LINE;
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
        return new LineGeometry(FXCollections.observableArrayList(points));
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
