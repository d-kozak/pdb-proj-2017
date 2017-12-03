package cz.vutbr.fit.pdb.painter;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.geometry.LineGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import javafx.collections.FXCollections;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;

@Log
public class LinePainterState extends AbstractPainterState {

    private List<Point> points = new ArrayList<>();


    public LinePainterState(GraphicsContext graphics, EntityService entityService, Configuration configuration) {
        super(graphics, entityService, configuration);
    }

    public static void drawLine(GraphicsContext graphics, Entity entity) {
        drawLine(graphics, entity.getColor(), ((List<Point>) entity.getGeometry()
                                                                   .getDescription()));
    }

    private static void drawLine(GraphicsContext graphics, Color drawingColor, List<Point> points) {
        graphics.setFill(drawingColor);
        graphics.setStroke(drawingColor);
        for (int i = 1; i < points.size(); i++) {
            Point start = points.get(i - 1);
            Point end = points.get(i);
            graphics.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
        }
    }

    @Override
    public void clicked(double x, double y) {
        points.add(new Point(x, y));
    }

    @Override
    public void drawingFinished() {
        if (points.size() <= 1) {
            log.warning("Cannot draw a line consisting of a single point or less...");
            return;
        }

        Entity entity = new Entity();
        entity.setGeometry(new LineGeometry(FXCollections.observableArrayList(points)));
        drawLine(getGraphics(), getConfiguration().getDrawingColor(), points);
        getEntityService().addEntity(entity);
        points.clear();
    }
}
