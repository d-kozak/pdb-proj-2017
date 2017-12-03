package cz.vutbr.fit.pdb.entity.geometry;

import cz.vutbr.fit.pdb.configuration.DrawingMode;

import java.util.ArrayList;
import java.util.List;

public class LineGeometry implements EntityGeometry {

    private List<Point> points;

    public LineGeometry(List<Point> points) {
        this.points = new ArrayList<>(points);
    }

    @Override
    public boolean containsPoint(double x, double y) {
        throw new RuntimeException("Not impl yet");
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
