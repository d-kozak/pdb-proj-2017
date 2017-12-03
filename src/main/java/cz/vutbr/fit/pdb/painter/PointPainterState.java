package cz.vutbr.fit.pdb.painter;

import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.entity.geometry.PointGeometry;
import javafx.scene.canvas.GraphicsContext;
import lombok.extern.java.Log;

@Log
public class PointPainterState extends AbstractPainterState {
    private static final int radius = 5;

    public PointPainterState(GraphicsContext graphics, EntityService entityService) {
        super(graphics, entityService);
    }

    public static void drawPoint(GraphicsContext graphics, EntityGeometry geometry) {
        Point description = (Point) geometry.getDescription();
        double x = description.getX();
        double y = description.getY();
        log.info(String.format("Drawing point at [%f,%f]", x, y));
        graphics.fillOval(x, y, radius, radius);
    }

    @Override
    public void clicked(double x, double y) {
        log.info(String.format("Drawing point at [%f,%f]", x, y));
        getGraphics().fillOval(x, y, radius, radius);
        Entity entity = new Entity();
        entity.setGeometry(new PointGeometry(new Point(x, y)));
        getEntityService().addEntity(entity);
    }
}
