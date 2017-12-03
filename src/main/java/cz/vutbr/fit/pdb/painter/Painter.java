package cz.vutbr.fit.pdb.painter;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.configuration.DrawingMode;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;


public class Painter {

    private GraphicsContext graphics;
    private EntityService entityService;
    private Configuration configuration;

    private PainterState painterState;

    public Painter(GraphicsContext graphics, EntityService entityService, Configuration configuration) {
        this.graphics = graphics;
        this.entityService = entityService;
        this.configuration = configuration;
        this.painterState = new PointPainterState(graphics, entityService, configuration);
        this.configuration.drawModeProperty()
                          .addListener(this::drawModeChanged);
    }

    private void drawModeChanged(ObservableValue<? extends DrawingMode> observable, DrawingMode oldValue, DrawingMode newValue) {
        switch (newValue) {
            case POINT:
                this.painterState = new PointPainterState(graphics, entityService, configuration);
                break;
            case LINE:
                this.painterState = new LinePainterState(graphics, entityService, configuration);
                break;
            case CIRCLE:
                this.painterState = new CirclePainterState(graphics, entityService, configuration);
                break;
            case POLYGON:
                this.painterState = new PolygonPainterState(graphics, entityService, configuration);
                break;
            default:
                throw new RuntimeException("Default at switch");
        }
    }

    public void paintAll(ObservableList<Entity> entities) {
        for (Entity entity : entities) {
            graphics.setFill(entity.getColor());
            switch (entity.getGeometryType()) {
                case POINT:
                    PointPainterState.drawPoint(graphics, entity);
                    break;
                case LINE:
                    LinePainterState.drawLine(graphics, entity);
                    break;
                case CIRCLE:
                    CirclePainterState.drawCircle(graphics, entity);
                    break;
                case POLYGON:
                    PolygonPainterState.drawPolygon(graphics, entity);
                    break;
                default:
                    throw new RuntimeException("Default at switch");
            }
        }
    }

    public void clicked(double x, double y) {
        this.painterState.clicked(x, y);
    }

    public void drawingFinished() {
        painterState.drawingFinished();
    }
}
