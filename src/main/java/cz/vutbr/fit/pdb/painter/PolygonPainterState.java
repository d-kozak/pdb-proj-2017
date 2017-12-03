package cz.vutbr.fit.pdb.painter;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import javafx.scene.canvas.GraphicsContext;

public class PolygonPainterState extends AbstractPainterState {


    public PolygonPainterState(GraphicsContext graphics, EntityService entityService, Configuration configuration) {
        super(graphics, entityService, configuration);
    }

    @Override
    public void clicked(double x, double y) {

    }

    public static void drawPolygon(GraphicsContext graphics, Entity entity) {

    }

    @Override
    public void drawingFinished() {

    }
}
