package cz.vutbr.fit.pdb.entity.geometry;

import cz.vutbr.fit.pdb.configuration.DrawingMode;

public interface EntityGeometry {
    DrawingMode getType();

    Object getDescription();

    EntityGeometry copyOf();
}
