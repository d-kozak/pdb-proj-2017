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
        this.painterState = new PointPainterState(graphics, entityService);
        this.configuration.drawModeProperty()
                          .addListener(this::drawModeChanged);

    }

    private void drawModeChanged(ObservableValue<? extends DrawingMode> observable, DrawingMode oldValue, DrawingMode newValue) {
        switch (newValue) {
            case POINT:
                this.painterState = new PointPainterState(graphics, entityService);
                break;
            case LINE:
                this.painterState = new LinePainterState(graphics, entityService);
                break;
            case CIRCLE:
                this.painterState = new CirclePainterState(graphics, entityService);
                break;
            case POLYGON:
                this.painterState = new PolygonPainterState(graphics, entityService);
                break;
            default:
                throw new RuntimeException("Default at switch");
        }
    }

    public void paintAll(ObservableList<Entity> entities) {
        for (Entity entity : entities) {
            switch (entity.getGeometryType()) {
                case POINT:
                    PointPainterState.drawPoint(graphics, entity.getGeometry());
                    break;
                case LINE:
                    LinePainterState.drawLine(graphics, entity.getGeometry());
                    break;
                case CIRCLE:
                    CirclePainterState.drawCircle(graphics, entity.getGeometry());
                    break;
                case POLYGON:
                    PolygonPainterState.drawPolygon(graphics, entity.getGeometry());
                    break;
                default:
                    throw new RuntimeException("Default at switch");
            }
        }
    }

    public void clicked(double x, double y) {
        this.painterState.clicked(x, y);
    }
}
