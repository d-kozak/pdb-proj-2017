package cz.vutbr.fit.pdb.entity;

import cz.vutbr.fit.pdb.utils.DummyData;
import javafx.collections.ObservableList;
import lombok.extern.java.Log;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;

@Log
public class EntityService {

    @Inject
    private SelectedEntityService selectedEntityService;
    private ObservableList<Entity> entities = DummyData.getEntities();

    @PostConstruct
    public void init() {
        selectedEntityService.setEntityProperty(entities.get(0));
    }

    public void addEntity(Entity entity) {
        log.info(String.format("Adding new entity %s", entity));
        entities.add(entity);
    }

    public ObservableList<Entity> getEntities() {
        return entities;
    }

    public void tryToSelectEntityAt(double x, double y) {
        Optional<Entity> entityAt = entities.stream()
                                            .filter(entity -> entity.getGeometry()
                                                                    .containsPoint(x, y))
                                            .findFirst();
        entityAt.ifPresent(entity -> {
            log.info(String.format("Changing entity from %s to %s", selectedEntityService.getEntityProperty(), entity));
            selectedEntityService.setEntityProperty(entity);
        });
    }

    public void forceRefreshEntityList() {
        ObservableList<Entity> entities = getEntities();
        if (!entities.isEmpty()) {
            Entity entity = entities.get(0);
            entities.remove(0);
            entities.add(0, entity);
        }
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }
}
