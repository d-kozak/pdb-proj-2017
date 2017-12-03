package cz.vutbr.fit.pdb.painter;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.geometry.CircleGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.utils.MathUtils;
import javafx.beans.property.DoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CirclePainterState extends AbstractPainterState {
    private Point center;

    public CirclePainterState(GraphicsContext graphics, EntityService entityService, Configuration configuration) {
        super(graphics, entityService, configuration);
    }

    public static void drawCircle(GraphicsContext graphics, Entity entity) {
        graphics.setFill(entity.getColor());
        graphics.setStroke(entity.getColor());
        Object[] description = (Object[]) entity.getGeometry()
                                                .getDescription();
        Point center = ((Point) description[0]);
        DoubleProperty doubleProperty = ((DoubleProperty) description[1]);
        graphics.fillOval(center.getX(), center.getY(), doubleProperty.doubleValue(), doubleProperty.doubleValue());
    }

    @Override
    public void clicked(double x, double y) {
        if (center == null) {
            center = new Point(x, y);
        } else {
            double radius = MathUtils.distance(center.getX(), center.getY(), x, y);
            Entity entity = new Entity();
            Color drawingColor = getConfiguration().getDrawingColor();
            entity.setGeometry(new CircleGeometry(center, radius));
            entity.setColor(drawingColor);
            getGraphics().setFill(drawingColor);
            getGraphics().setStroke(drawingColor);
            getGraphics().fillOval(center.getX(), center.getY(), radius, radius);
            center = null;
        }
    }

    @Override
    public void drawingFinished() {
        // nothing to do
    }
}
