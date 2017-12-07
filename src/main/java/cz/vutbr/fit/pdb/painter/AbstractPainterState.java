package cz.vutbr.fit.pdb.painter;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import javafx.scene.canvas.GraphicsContext;

import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showInfo;

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

    protected void addEntity(Entity entity) {
        getEntityService().addEntity(
                entity,
                () -> showInfo("Entity added", "Entity added successfully"),
                () -> {
                    showError("Database error", "Sorry, could not add the entity, please try again");
                    configuration.getMapRenderer()
                                 .redraw();
                });
    }
}
