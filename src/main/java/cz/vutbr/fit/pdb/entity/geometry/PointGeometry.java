package cz.vutbr.fit.pdb.entity.geometry;

import cz.vutbr.fit.pdb.configuration.DrawingMode;

public class PointGeometry implements EntityGeometry {

    private Point point;

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
        return false;
    }
}
