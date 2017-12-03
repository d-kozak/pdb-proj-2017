package cz.vutbr.fit.pdb.entity.geometry;

import cz.vutbr.fit.pdb.configuration.DrawingMode;

public interface EntityGeometry {
    DrawingMode getType();

    Object getDescription();

    boolean containsPoint(double x, double y);
}
