package cz.vutbr.fit.pdb.painter;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.geometry.LineGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.extern.java.Log;

import static java.lang.String.format;

@Log
public class LinePainterState extends AbstractPainterState {

    private Point start;


    public LinePainterState(GraphicsContext graphics, EntityService entityService, Configuration configuration) {
        super(graphics, entityService, configuration);
    }

    public static void drawLine(GraphicsContext graphics, Entity entity) {
        graphics.setFill(entity.getColor());
        graphics.setStroke(entity.getColor());
        Point[] description = (Point[]) entity.getGeometry()
                                              .getDescription();
        graphics.strokeLine(description[0].getX(), description[0].getY(), description[1].getX(), description[1].getY());
    }

    @Override
    public void clicked(double x, double y) {
        if (start == null) {
            log.info(format("Start the line at [%s,%s]", x, y));
            start = new Point(x, y);
        } else {
            log.info(format("Drawing line from [%f,%f] to [%f,%f]", start.getX(), start.getY(), x, y));
            Color drawingColor = getConfiguration().getDrawingColor();
            getGraphics().setFill(drawingColor);
            getGraphics().setStroke(drawingColor);
            getGraphics().strokeLine(start.getX(), start.getY(), x, y);
            Point end = new Point(x, y);
            Entity entity = new Entity();
            entity.setColor(drawingColor);
            entity.setGeometry(new LineGeometry(start, end));
            getEntityService().addEntity(entity);
            start = null;
        }
    }
}
