package cz.vutbr.fit.pdb.painter;

import javafx.scene.canvas.GraphicsContext;
import lombok.extern.java.Log;

@Log
public class PointPainterState extends AbstractPainterState {
    private static final int radius = 5;

    public PointPainterState(GraphicsContext graphics2D) {
        super(graphics2D);
    }

    @Override
    public void clicked(double x, double y) {
        log.info(String.format("Drawing point at [%f,%f]", x, y));
        getGraphics().fillOval(x, y, radius, radius);
    }
}
