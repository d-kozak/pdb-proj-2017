package cz.vutbr.fit.pdb.entity;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.concurent.AddEntityTask;
import cz.vutbr.fit.pdb.entity.concurent.LoadAllEntitiesTask;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static cz.vutbr.fit.pdb.configuration.Configuration.THREAD_POOL;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showInfo;

@Log
public class EntityService {

    @Inject
    private SelectedEntityService selectedEntityService;

    private ObservableList<Entity> entities = FXCollections.observableArrayList(entity -> new Observable[]{entity.nameProperty()});

    private BooleanProperty initDataLoaded = new SimpleBooleanProperty(false);

    @PostConstruct
    public void init() {
        LoadAllEntitiesTask loadAllEntitiesTask = new LoadAllEntitiesTask();
        loadAllEntitiesTask.setOnSucceeded(event -> {
            try {
                entities.clear();
                entities.addAll(loadAllEntitiesTask.get());
                if (!entities.isEmpty()) {
                    selectedEntityService.setEntityProperty(entities.get(0));
                }
                log.info("Loaded entities: " + entities);
                initDataLoaded.set(true);

                showInfo("Success", "Entities loaded successfully");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        loadAllEntitiesTask.setOnFailed(event -> {
            log.severe("Couldn't load entities");
            showError("Database error", "Could not load entities");
        });
        THREAD_POOL.submit(loadAllEntitiesTask);
    }

    public Task<Void> addEntity(Entity entity, Runnable onSucceeded, Runnable onFailed) {
        log.info(String.format("Adding new entity %s", entity));
        AddEntityTask addEntityTask = new AddEntityTask();
        addEntityTask.setEntity(entity);
        addEntityTask.setOnSucceeded(event -> {
            onSucceeded.run();
            entities.add(entity);
        });
        addEntityTask.setOnFailed(event -> onFailed.run());

        Configuration.THREAD_POOL.submit(addEntityTask);
        return addEntityTask;
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

    public ObservableList<Entity> getEntities(int selectedYear) {
        log.info("All entities :" + entities);
        log.info("Selecting year " + selectedYear);
        FilteredList<Entity> selected = entities.filtered(entity -> entity.existsInYear(selectedYear));
        log.info("Selected entities :" + selected);
        return selected;
    }
}
