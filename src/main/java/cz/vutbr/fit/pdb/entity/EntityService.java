package cz.vutbr.fit.pdb.entity;

import cz.vutbr.fit.pdb.entity.concurent.LoadAllEntitiesTask;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.java.Log;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static cz.vutbr.fit.pdb.configuration.Configuration.THREAD_POOL;

@Log
public class EntityService {

    @Inject
    private SelectedEntityService selectedEntityService;
    private ObservableList<Entity> entities = FXCollections.observableArrayList();

    private BooleanProperty initDataLoaded = new SimpleBooleanProperty(false);

    private EntityUpdater entityUpdater = new EntityUpdater(this);

    @PostConstruct
    public void init() {
        LoadAllEntitiesTask loadAllEntitiesTask = new LoadAllEntitiesTask();
        loadAllEntitiesTask.setOnSucceeded(event -> {
            try {
                entities.addAll(loadAllEntitiesTask.get());
                if (!entities.isEmpty()) {
                    selectedEntityService.setEntityProperty(entities.get(0));
                }
                log.info("Loaded entities: " + entities);
                initDataLoaded.set(true);
                entityUpdater.addListeners(entities);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        loadAllEntitiesTask.setOnFailed(event -> {
            log.severe("Couldn't load entities");
        });
        THREAD_POOL.submit(loadAllEntitiesTask);
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

    public boolean isInitDataLoaded() {
        return initDataLoaded.get();
    }

    public void setInitDataLoaded(boolean initDataLoaded) {
        this.initDataLoaded.set(initDataLoaded);
    }

    public BooleanProperty initDataLoadedProperty() {
        return initDataLoaded;
    }
}
