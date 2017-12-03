package cz.vutbr.fit.pdb.entity.geometry;

import cz.vutbr.fit.pdb.configuration.DrawingMode;

public class LineGeometry implements EntityGeometry {

    private Point start;
    private Point end;

    public LineGeometry(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean containsPoint(double x, double y) {
        return false;
    }

    @Override
    public DrawingMode getType() {
        return DrawingMode.LINE;
    }

    @Override
    public Object getDescription() {
        return new Point[]{start, end};
    }
}
