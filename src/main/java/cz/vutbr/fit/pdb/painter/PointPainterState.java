package cz.vutbr.fit.pdb.painter;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.entity.geometry.PointGeometry;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.extern.java.Log;

@Log
public class PointPainterState extends AbstractPainterState {
    public static final int POINT_RADIUS = 5;

    public PointPainterState(GraphicsContext graphics, EntityService entityService, Configuration configuration) {
        super(graphics, entityService, configuration);
        configuration.drawingColorProperty()
                     .addListener((observable, oldValue, newValue) -> {
                         graphics.setFill(newValue);
                     });
    }

    public static void drawPoint(GraphicsContext graphics, Entity entity) {
        EntityGeometry geometry = entity.getGeometry();
        graphics.setFill(entity.getColor());
        Point description = (Point) geometry.getDescription();
        double x = description.getX();
        double y = description.getY();
        log.info(String.format("Drawing point at [%f,%f]", x, y));
        graphics.fillOval(x, y, POINT_RADIUS, POINT_RADIUS);
    }

    @Override
    public void clicked(double x, double y) {
        log.info(String.format("Drawing point at [%f,%f]", x, y));
        getGraphics().fillOval(x, y, POINT_RADIUS, POINT_RADIUS);
        Entity entity = new Entity();
        entity.setGeometry(new PointGeometry(new Point(x, y)));
        entity.setColor(((Color) getGraphics().getFill()));
        entity.setName("New point");
        getEntityService().addEntity(entity);
    }

    @Override
    public void drawingFinished() {
        // nothing to do
    }
}
