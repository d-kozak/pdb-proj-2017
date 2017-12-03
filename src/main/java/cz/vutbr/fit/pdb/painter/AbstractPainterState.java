package cz.vutbr.fit.pdb.painter;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.EntityService;
import javafx.scene.canvas.GraphicsContext;

public abstract class AbstractPainterState implements PainterState {
    private GraphicsContext graphics;
    private EntityService entityService;
    private Configuration configuration;

    public AbstractPainterState(GraphicsContext graphics, EntityService entityService, Configuration configuration) {
        this.graphics = graphics;
        this.entityService = entityService;
        this.configuration = configuration;
    }

    protected Configuration getConfiguration() {
        return configuration;
    }

    protected GraphicsContext getGraphics() {
        return graphics;
    }

    protected EntityService getEntityService() {
        return entityService;
    }
}
