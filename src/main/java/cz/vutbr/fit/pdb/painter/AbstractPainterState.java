package cz.vutbr.fit.pdb.painter;

import cz.vutbr.fit.pdb.entity.EntityService;
import javafx.scene.canvas.GraphicsContext;

public abstract class AbstractPainterState implements PainterState {
    private GraphicsContext graphics;
    private EntityService entityService;

    public AbstractPainterState(GraphicsContext graphics, EntityService entityService) {
        this.graphics = graphics;
        this.entityService = entityService;
    }

    protected GraphicsContext getGraphics() {
        return graphics;
    }

    protected EntityService getEntityService() {
        return entityService;
    }
}
