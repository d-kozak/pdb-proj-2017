package cz.vutbr.fit.pdb.painter;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.configuration.DrawingMode;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.GraphicsContext;


public class Painter {

    private GraphicsContext graphics;
    private Configuration configuration;

    private PainterState painterState;

    public Painter(GraphicsContext graphics, Configuration configuration) {
        this.graphics = graphics;
        this.configuration = configuration;
        this.painterState = new PointPainterState(graphics);
        this.configuration.drawModeProperty()
                          .addListener(this::drawModeChanged);

    }

    private void drawModeChanged(ObservableValue<? extends DrawingMode> observable, DrawingMode oldValue, DrawingMode newValue) {
        switch (newValue) {
            case POINT:
                this.painterState = new PointPainterState(graphics);
                break;
            case LINE:
                this.painterState = new LinePainterState(graphics);
                break;
            case CIRCLE:
                this.painterState = new CirclePainterState(graphics);
                break;
            case POLYGON:
                this.painterState = new PolygonPainterState(graphics);
                break;
            default:
                throw new RuntimeException("Default at switch");
        }
    }

    public void clicked(double x, double y) {
        this.painterState.clicked(x, y);
    }
}
