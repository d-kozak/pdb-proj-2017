package cz.vutbr.fit.pdb.entity.geometry;

import cz.vutbr.fit.pdb.configuration.DrawingMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.java.Log;

import static cz.vutbr.fit.pdb.utils.MathUtils.distance;
import static java.lang.String.format;

@Log
public class RectangleGeometry extends PolygonGeometry {
    public RectangleGeometry(ObservableList<Point> points) {
        super(points);
    }

    public DrawingMode getType() {
        return DrawingMode.RECTANGLE;
    }
}

