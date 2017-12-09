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
        DoubleProperty radius = ((DoubleProperty) description[1]);
        graphics.fillOval(center.getX() - radius.get(), center.getY() - radius.get(), 2 * radius.get(), 2 * radius.get());
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
            entity.setName("New circle");
            getGraphics().setFill(drawingColor);
            getGraphics().setStroke(drawingColor);
            getGraphics().fillOval(center.getX() - radius, center.getY() - radius, 2 * radius, 2 * radius);
            addEntity(entity);
            center = null;
        }

    }

    @Override
    public void drawingFinished() {
        // nothing to do
    }
}
