package cz.vutbr.fit.pdb.painter;

import javafx.scene.canvas.GraphicsContext;

public abstract class AbstractPainterState implements PainterState {
    private GraphicsContext graphics;

    public AbstractPainterState(GraphicsContext graphics) {
        this.graphics = graphics;
    }

    protected GraphicsContext getGraphics() {
        return graphics;
    }
}
