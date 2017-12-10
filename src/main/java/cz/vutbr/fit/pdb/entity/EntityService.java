package cz.vutbr.fit.pdb.entity;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.concurent.AddEntityTask;
import cz.vutbr.fit.pdb.entity.concurent.LoadAllEntitiesTask;
import cz.vutbr.fit.pdb.entity.concurent.RemoveEntityTask;
import cz.vutbr.fit.pdb.entity.concurent.UpdateEntityTask;
import cz.vutbr.fit.pdb.entity.concurent.picture.AddPictureTask;
import cz.vutbr.fit.pdb.entity.concurent.picture.DeleteFlagTask;
import cz.vutbr.fit.pdb.entity.concurent.picture.RemovePictureTask;
import cz.vutbr.fit.pdb.entity.concurent.picture.SetAsFlagTask;
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
    SelectedEntityService selectedEntityService;

    private ObservableList<Entity> entities = FXCollections.observableArrayList(entity -> new Observable[]{entity.nameProperty()});

    private BooleanProperty initDataLoaded = new SimpleBooleanProperty(false);

    @PostConstruct
    public void init() {
        LoadAllEntitiesTask loadAllEntitiesTask = new LoadAllEntitiesTask();
        loadAllEntitiesTask.setOnSucceeded(event -> {
            try {
                entities.clear();
                entities.addAll(loadAllEntitiesTask.get());
                for (Entity entity : entities) {
                    entity.entityService = this;
                }
                log.info("Loaded entities: " + entities);
                initDataLoaded.set(true);

                showInfo("Success", "Entities loaded successfully");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        loadAllEntitiesTask.setOnFailed(event -> {
            printException(loadAllEntitiesTask.getException());
            showError("Database error", "Could not load entities");
        });
        THREAD_POOL.submit(loadAllEntitiesTask);
    }

    public Task<Void> addEntity(Entity entity, Runnable onSucceeded, Runnable onFailed) {
        log.info(String.format("Adding new entity %s", entity));
        entity.entityService = this;
        AddEntityTask addEntityTask = new AddEntityTask();
        addEntityTask.setEntity(entity);
        addEntityTask.setOnSucceeded(event -> {
            onSucceeded.run();
            entities.add(entity);
            selectedEntityService.setEntityProperty(entity);
        });
        addEntityTask.setOnFailed(event -> {
            printException(addEntityTask.getException());
            onFailed.run();
        });

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

    public Task<Void> removeEntity(Entity entity, Runnable onSucceeded, Runnable onFailed) {
        RemoveEntityTask removeEntityTask = new RemoveEntityTask();
        removeEntityTask.setEntity(entity);
        removeEntityTask.setOnSucceeded(event -> {
            onSucceeded.run();
            entities.remove(entity);
        });
        removeEntityTask.setOnFailed(event -> {
            printException(removeEntityTask.getException());
            onFailed.run();
        });
        Configuration.THREAD_POOL.submit(removeEntityTask);
        return removeEntityTask;
    }

    public <T> Task<Void> updateEntity(Entity entity, String fieldName, Runnable onSucceeded, Runnable onFailed) {
        UpdateEntityTask updateEntityTask = new UpdateEntityTask();
        updateEntityTask.setEntity(entity);
        updateEntityTask.setFieldName(fieldName);
        updateEntityTask.setOnSucceeded(event -> onSucceeded.run());
        updateEntityTask.setOnFailed(event -> {
            printException(updateEntityTask.getException());
            onFailed.run();
        });

        Configuration.THREAD_POOL.submit(updateEntityTask);
        return updateEntityTask;
    }


    public Task<Void> addImage(int entityId, EntityImage entityImage, Runnable onSucceeded, Runnable onFailed) {
        AddPictureTask addPictureTask = new AddPictureTask();
        addPictureTask.setEntityId(entityId);
        addPictureTask.setEntityImage(entityImage);
        addPictureTask.setOnSucceeded(event -> {
            onSucceeded.run();
        });
        addPictureTask.setOnFailed(event -> {
            printException(addPictureTask.getException());
            onFailed.run();
        });

        Configuration.THREAD_POOL.submit(addPictureTask);
        return addPictureTask;
    }

    public Task<Void> removeImage(EntityImage entityImage, Runnable onSucceeded, Runnable onFailed) {
        RemovePictureTask removeImageTask = new RemovePictureTask();
        removeImageTask.setEntityImage(entityImage);
        removeImageTask.setOnSucceeded(event -> {
            onSucceeded.run();
        });
        removeImageTask.setOnFailed(event -> {
            printException(removeImageTask.getException());
            onFailed.run();
        });

        Configuration.THREAD_POOL.submit(removeImageTask);
        return removeImageTask;
    }

    public Task<Void> setAsFlag(int entityId, EntityImage entityImage, Runnable onSucceeded, Runnable onFailed) {
        SetAsFlagTask setAsFlagTask = new SetAsFlagTask();
        setAsFlagTask.setEntityImage(entityImage);
        setAsFlagTask.setEntityId(entityId);
        setAsFlagTask.setOnSucceeded(event -> {
            onSucceeded.run();
        });
        setAsFlagTask.setOnFailed(event -> {
            printException(setAsFlagTask.getException());
            onFailed.run();
        });

        Configuration.THREAD_POOL.submit(setAsFlagTask);
        return setAsFlagTask;
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

    public void printException(Throwable exception) {
        log.severe("Task failed with exception:" + exception);
        exception.printStackTrace();
    }

    public DeleteFlagTask deleteFlag(EntityImage entityImage, Runnable onSucceeded, Runnable onFailed) {
        DeleteFlagTask deleteFlagTask = new DeleteFlagTask();
        deleteFlagTask.setEntityImage(entityImage);
        deleteFlagTask.setOnSucceeded(event -> {
            onSucceeded.run();
        });
        deleteFlagTask.setOnFailed(event -> {
            printException(deleteFlagTask.getException());
            onFailed.run();
        });
        Configuration.THREAD_POOL.submit(deleteFlagTask);
        return deleteFlagTask;
    }
}
