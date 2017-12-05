package cz.vutbr.fit.pdb.painter;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.entity.geometry.PolygonGeometry;
import javafx.collections.FXCollections;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class PolygonPainterState extends AbstractPainterState {

    private List<Point> points = new ArrayList<>();

    public PolygonPainterState(GraphicsContext graphics, EntityService entityService, Configuration configuration) {
        super(graphics, entityService, configuration);
    }

    public static void drawPolygon(GraphicsContext graphics, Entity entity) {
        drawPolygon(graphics, entity.getColor(), ((List<Point>) entity.getGeometry()
                                                                      .getDescription()));
    }

    private static void drawPolygon(GraphicsContext graphics, Color color, List<Point> points) {
        graphics.setStroke(color);
        graphics.setFill(color);

        double[] xPoints = new double[points.size()];
        double[] yPoints = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            xPoints[i] = point.getX();
            yPoints[i] = point.getY();
        }
        graphics.fillPolygon(xPoints, yPoints, points.size());
    }

    @Override
    public void clicked(double x, double y) {
        points.add(new Point(x, y));
    }

    @Override
    public void drawingFinished() {
        Entity entity = new Entity();
        Color drawingColor = getConfiguration().getDrawingColor();
        entity.setColor(drawingColor);
        entity.setGeometry(new PolygonGeometry(FXCollections.observableArrayList(points)));
        entity.setName("New polygon");
        drawPolygon(getGraphics(), drawingColor, points);
        getEntityService().addEntity(entity);
        points.clear();
    }
}
