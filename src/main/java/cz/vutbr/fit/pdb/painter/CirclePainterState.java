package cz.vutbr.fit.pdb.painter;

import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import javafx.scene.canvas.GraphicsContext;

public class CirclePainterState extends AbstractPainterState {
    public CirclePainterState(GraphicsContext graphics, EntityService entityService) {
        super(graphics, entityService);
    }

    @Override
    public void clicked(double x, double y) {

    }

    public static void drawCircle(GraphicsContext graphics, EntityGeometry geometry) {

    }
}
