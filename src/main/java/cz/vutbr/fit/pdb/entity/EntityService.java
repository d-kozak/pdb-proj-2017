package cz.vutbr.fit.pdb.entity;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.concurent.AddEntityTask;
import cz.vutbr.fit.pdb.entity.concurent.LoadAllEntitiesTask;
import cz.vutbr.fit.pdb.entity.concurent.RemoveEntityTask;
import cz.vutbr.fit.pdb.entity.concurent.UpdateEntityTask;
import cz.vutbr.fit.pdb.entity.concurent.geometry.SelectEntitiesAtTask;
import cz.vutbr.fit.pdb.entity.concurent.picture.*;
import cz.vutbr.fit.pdb.entity.geometry.Point;
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
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static cz.vutbr.fit.pdb.configuration.Configuration.THREAD_POOL;
import static cz.vutbr.fit.pdb.utils.ExceptionUtils.printException;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showInfo;
import static java.util.stream.Collectors.joining;

@Log
public class EntityService {

    @Inject
    SelectedEntityService selectedEntityService;

    @Inject
    private Configuration configuration;

    private ObservableList<Entity> entities = FXCollections.observableArrayList(entity -> new Observable[]{entity.nameProperty()});

    private BooleanProperty initDataLoaded = new SimpleBooleanProperty(false);

    @PostConstruct
    public void init() {
        initDataLoaded.set(false);
        LoadAllEntitiesTask loadAllEntitiesTask = new LoadAllEntitiesTask();
        loadAllEntitiesTask.setOnSucceeded(event -> {
            try {
                entities.clear();
                entities.addAll(loadAllEntitiesTask.get());
                for (Entity entity : entities) {
                    entity.entityService = this;
                }
                String entitiesFormatted = entities.stream()
                                                   .map(Entity::toString)
                                                   .collect(joining("\n\n"));
                log.info("Loaded entities: \n" + entitiesFormatted);
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

    public Task<Entity> addEntity(Entity entity, Runnable onSucceeded, Runnable onFailed) {
        log.info(String.format("Adding new entity %s", entity));
        entity.entityService = this;
        AddEntityTask addEntityTask = new AddEntityTask();
        addEntityTask.setEntity(entity);
        addEntityTask.setOnSucceeded(event -> {
            onSucceeded.run();
            Entity newEntity = addEntityTask.getValue();
            newEntity.entityService = this;
            entities.add(newEntity);
            selectedEntityService.setEntityProperty(newEntity);
            configuration.getMapRenderer()
                         .redraw();
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

    public Task<ObservableList<Integer>> tryToSelectEntityAt(double x, double y) {
        log.info(String.format("Trying to select entity at [%f,%f]", x, y));
        SelectEntitiesAtTask selectEntitiesAtTask = new SelectEntitiesAtTask();
        selectEntitiesAtTask.setPoint(new Point(x, y));
        selectEntitiesAtTask.setOnSucceeded(event -> {
            Map<Integer, Entity> entitiesMap = new HashMap<>();
            for (Entity entity : entities) {
                entitiesMap.put(entity.getId(), entity);
            }
            ObservableList<Integer> loadedEntities = selectEntitiesAtTask.getValue();
            log.info("Entities at are: " + loadedEntities.stream()
                                                         .map(entitiesMap::get)
                                                         .map(Entity::getName)
                                                         .collect(joining("\n\n")));
            Optional<Entity> entityAt = loadedEntities.stream()
                                                      .map(entitiesMap::get)
                                                      .sorted(Comparator.comparingInt(value -> value.getGeometryType()
                                                                                                    .ordinal()))
                                                      .findFirst();
            entityAt.ifPresent(entity -> {
                log.info(String.format("Changing entity from %s to %s", selectedEntityService.getEntityProperty(), entity));
                selectedEntityService.setEntityProperty(entity);
                showInfo("Entity selected", "Entity " + entity.getName() + " selected");
            });
            if (!entityAt.isPresent()) {
                log.info("No entity found at [" + x + "," + y + "]");
            }
        });
        selectEntitiesAtTask.setOnFailed(event -> {
            printException(selectEntitiesAtTask.getException());
            showError("Database errror", "Could not select an entity at [" + x + "," + y + "]");
        });
        Configuration.THREAD_POOL.submit(selectEntitiesAtTask);
        return selectEntitiesAtTask;
    }

    public Task<Void> removeEntity(Entity entity, Runnable onSucceeded, Runnable onFailed) {
        RemoveEntityTask removeEntityTask = new RemoveEntityTask();
        removeEntityTask.setEntity(entity);
        removeEntityTask.setOnSucceeded(event -> {
            entities.remove(entity);
            onSucceeded.run();
        });
        removeEntityTask.setOnFailed(event -> {
            printException(removeEntityTask.getException());
            onFailed.run();
        });
        Configuration.THREAD_POOL.submit(removeEntityTask);
        return removeEntityTask;
    }

    public Task<Entity> updateEntity(Entity entity, String fieldName, Consumer<Entity> onSucceeded, Runnable onFailed) {
        UpdateEntityTask updateEntityTask = new UpdateEntityTask();
        updateEntityTask.setEntity(entity);
        updateEntityTask.setFieldName(fieldName);
        updateEntityTask.setOnSucceeded(event -> onSucceeded.accept(updateEntityTask.getValue()));
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
        log.info("Selecting entities from year " + selectedYear);
        FilteredList<Entity> selected = entities.filtered(entity -> entity.existsInYear(selectedYear));
        String prettyEntities = selected.stream()
                                        .map(Entity::toString)
                                        .collect(joining("\n\n"));
        log.info("Selected entities :" + prettyEntities);
        return selected;
    }


    public Task<ObservableList<EntityImage>> getTwoSimilarImagesFor(EntityImage entityImage, Consumer<ObservableList<EntityImage>> onSucceeded, Runnable onFailed) {
        GetSimilarPicturesTask getSimilarPicturesTask = new GetSimilarPicturesTask();
        getSimilarPicturesTask.setEntityImage(entityImage);
        getSimilarPicturesTask.setOnSucceeded(event -> {
            onSucceeded.accept(getSimilarPicturesTask.getValue());
        });
        getSimilarPicturesTask.setOnFailed(event -> {
            printException(getSimilarPicturesTask.getException());
            onFailed.run();
        });

        Configuration.THREAD_POOL.submit(getSimilarPicturesTask);

        return getSimilarPicturesTask;
    }

    public Task<EntityImage> editImage(EntityImage entityImage, ImageOperation imageOperation, Consumer<EntityImage> onSucceeded, Runnable onFailed) {
        ImageEditTask editImageTask = new ImageEditTask();
        editImageTask.setEntityImage(entityImage);
        editImageTask.setImageOperation(imageOperation);
        editImageTask.setOnSucceeded(event -> {
            onSucceeded.accept(editImageTask.getValue());
        });
        editImageTask.setOnFailed(event -> {
            printException(editImageTask.getException());
            onFailed.run();
        });
        Configuration.THREAD_POOL.submit(editImageTask);
        return editImageTask;
    }

    public Task<Void> deleteFlag(EntityImage entityImage, Runnable onSucceeded, Runnable onFailed) {
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

    public Configuration getConfiguration() {
        return configuration;
    }

    public void clearYear(int y) {
        getEntities(y).forEach((Entity ent) -> {
            Entity copy, copy2;
            int to = ent.getTo().getYear(), from = ent.getFrom().getYear();
            if(from < y) {
                copy = ent.copyOf();
                copy.setTo(LocalDate.of(y-1, 1,1));
                updateEntity(copy, "to", (newEntity) -> {
                    getEntities().remove(copy);
                    getEntities().add(newEntity);
                    getConfiguration()
                            .getMapRenderer()
                            .redraw();
                }, () -> {
                    showError("Database error", "Could not delete some of entities");
                });

                if(to > y) {
                    copy2 = ent.copyOf();
                    copy2.setFrom(LocalDate.of(y+2, 1,1));
                    addEntity(copy2, () -> {
                        getConfiguration()
                                .getMapRenderer()
                                .redraw();
                    }, () -> {
                        showError("Database error", "Could not delete some of entities");
                    });
                }
            } else if(to > y) {
                copy = ent.copyOf();
                copy.setFrom(LocalDate.of(y+1, 1,1));
                updateEntity(copy, "from", (newEntity) -> {
                    getEntities().remove(copy);
                    getEntities().add(newEntity);
                    getConfiguration()
                            .getMapRenderer()
                            .redraw();
                }, () -> {
                    showError("Database error", "Could not delete some of entities");
                });
            } else {
                removeEntity(
                        ent,
                        () -> {
                            if (selectedEntityService.getEntityProperty() == ent)
                                selectedEntityService.setEntityProperty(null);
                            configuration.getMapRenderer()
                                    .redraw();
                        },
                        () -> {
                            showError("Database error", "Could not remove entity, please try again");
                        });
            }
        });
    }
}
